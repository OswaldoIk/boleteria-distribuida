#!/usr/bin/env bash
set -euo pipefail
export PATH="/usr/pgsql-16/bin:$PATH"
export PGDATA="${PGDATA:-/var/lib/pgsql/16/data}"
mkdir -p "$(dirname "$PGDATA")"
chown -R postgres:postgres /var/lib/pgsql/16

if [[ ! -s "$PGDATA/PG_VERSION" ]]; then
  if [[ "${ROLE:?ROLE requerido}" == primary ]]; then
    runuser -u postgres -- initdb -D "$PGDATA" --encoding=UTF8 --locale=C.UTF-8 --auth-local=trust --auth-host=scram-sha-256
    cat >> "$PGDATA/postgresql.conf" <<EOF
listen_addresses='*'
wal_level=replica
max_wal_senders=10
max_replication_slots=10
wal_keep_size=256MB
hot_standby=on
cluster_name='${CLUSTER_NAME:-boleteria}'
password_encryption='scram-sha-256'
EOF
    printf 'host all all 172.28.0.0/16 scram-sha-256\nhost replication %s 172.28.0.0/16 scram-sha-256\n' "$REPLICATION_USER" >> "$PGDATA/pg_hba.conf"
    runuser -u postgres -- pg_ctl -D "$PGDATA" -o "-c listen_addresses=''" -w start
    psql -v ON_ERROR_STOP=1 -h /var/run/postgresql -U postgres \
      -v rp="$REPLICATION_PASSWORD" -v ap="$POSTGRES_PASSWORD" -v au="$POSTGRES_USER" -v db="$POSTGRES_DB" <<'SQL'
CREATE ROLE replicator WITH REPLICATION LOGIN PASSWORD :'rp';
CREATE ROLE :"au" WITH LOGIN PASSWORD :'ap';
CREATE DATABASE :"db" OWNER :"au";
GRANT pg_monitor TO :"au";
SQL
    for sql in /docker-entrypoint-initdb.d/*.sql; do
      [[ -f "$sql" ]] && psql -v ON_ERROR_STOP=1 -h /var/run/postgresql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f "$sql"
    done
    runuser -u postgres -- pg_ctl -D "$PGDATA" -m fast -w stop
  elif [[ "${ROLE}" == replica ]]; then
    until pg_isready -h "$PRIMARY_HOST" -p 5432; do sleep 2; done
    printf '%s:%s:%s:%s:%s\n' "$PRIMARY_HOST" 5432 replication "$REPLICATION_USER" "$REPLICATION_PASSWORD" > /var/lib/pgsql/.pgpass
    chown postgres:postgres /var/lib/pgsql/.pgpass
    chmod 600 /var/lib/pgsql/.pgpass
    export PGPASSFILE=/var/lib/pgsql/.pgpass
    runuser -u postgres -- env PGPASSFILE="$PGPASSFILE" pg_basebackup -h "$PRIMARY_HOST" -U "$REPLICATION_USER" -D "$PGDATA" -R -X stream -C -S "slot_${CLUSTER_NAME:-boleteria}"
    echo "cluster_name='${CLUSTER_NAME:-boleteria}'" >> "$PGDATA/postgresql.conf"
  else
    echo "ROLE debe ser primary o replica" >&2; exit 1
  fi
fi
exec setpriv --reuid=postgres --regid=postgres --init-groups postgres -D "$PGDATA"
