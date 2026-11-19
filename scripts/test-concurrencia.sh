#!/usr/bin/env bash
set -euo pipefail
if [[ $# -ne 3 ]]; then echo "Uso: $0 <UUID_LOCALIDAD> <N> <PARALELO>" >&2; exit 2; fi
id=$1 n=$2 paralelo=$3
(( n>0 && paralelo>0 )) || { echo "N y PARALELO deben ser positivos" >&2; exit 2; }
api=${API_URL:-http://localhost:8080}
tmp=$(mktemp -d); trap 'rm -rf "$tmp"' EXIT
seq "$n" | xargs -P "$paralelo" -I{} sh -c 'curl -sS -o /dev/null -w "%{http_code}\n" -X POST "$1/api/v1/reservas" -H "Content-Type: application/json" -d "{\"localidadId\":\"$2\",\"usuarioId\":\"concurrencia-{}\",\"cantidad\":1}" || echo 000' _ "$api" "$id" > "$tmp/codigos"
sort "$tmp/codigos" | uniq -c
docker compose exec -T nodo_centos_primary sh -lc 'psql -qAt -U "$POSTGRES_USER" -d "$POSTGRES_DB" -v ON_ERROR_STOP=1' < "$(dirname "$0")/verificar-integridad.sql" | tee "$tmp/integridad"
if grep -Eq 'cupos_negativos|reservas_superan_capacidad|asiento_multiples_reservas_vivas' "$tmp/integridad"; then echo 'Se detectaron inconsistencias' >&2; exit 1; fi
