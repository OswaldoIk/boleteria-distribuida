#!/usr/bin/env bash
set -euo pipefail
nuevo=${1:?Uso: $0 <nuevo-primario-servicio> <servicio-caido>}
caido=${2:?Uso: $0 <nuevo-primario-servicio> <servicio-caido>}
[[ "$nuevo" == nodo_centos_primary || "$nuevo" == nodo_centos_secondary ]] || { echo 'Nombre de servicio inválido' >&2; exit 2; }
[[ "$caido" == nodo_centos_primary || "$caido" == nodo_centos_secondary ]] || { echo 'Nombre de servicio inválido' >&2; exit 2; }
[[ "$nuevo" != "$caido" ]] || { echo 'Los nodos deben ser distintos' >&2; exit 2; }
case "$caido" in nodo_centos_primary) volume="pg_primary"; rolevar=PRIMARY_ROLE;; *) volume="pg_secondary"; rolevar=SECONDARY_ROLE;; esac
docker compose stop "$caido" || true
docker compose rm -f "$caido" || true
docker volume rm "$volume"
env "$rolevar=replica" PRIMARY_HOST="$nuevo" docker compose up -d --build "$caido"
echo 'Verifique pg_stat_replication en el nuevo primario:'
docker exec "$nuevo" psql -U "${POSTGRES_USER:-boleteria_app}" -d "${POSTGRES_DB:-boleteria}" -c 'select application_name,state,sync_state from pg_stat_replication;'
