#!/usr/bin/env bash
set -euo pipefail
start=$(date +%s)
id=${LOCALIDAD_ID:?Defina LOCALIDAD_ID con un UUID disponible}
echo "Deteniendo primario..."; docker stop nodo_centos_primary >/dev/null
until docker exec nodo_centos_secondary pg_isready -h 127.0.0.1 >/dev/null 2>&1; do sleep 1; done
docker exec nodo_centos_secondary psql -U "${POSTGRES_USER:-boleteria_app}" -d "${POSTGRES_DB:-boleteria}" -v ON_ERROR_STOP=1 -c 'select pg_promote();'
until [[ "$(docker exec nodo_centos_secondary psql -U "${POSTGRES_USER:-boleteria_app}" -d "${POSTGRES_DB:-boleteria}" -Atc 'select pg_is_in_recovery()')" == f ]]; do sleep 1; done
while true; do code=$(curl -sS -o /tmp/boleteria-failover.json -w '%{http_code}' -X POST "${API_URL:-http://localhost:8080}/api/v1/reservas" -H 'Content-Type: application/json' -d "{\"localidadId\":\"$id\",\"usuarioId\":\"prueba-failover\",\"cantidad\":1}" || true); [[ "$code" == 201 ]] && break; sleep 1; done
end=$(date +%s); echo "RTO: $((end-start)) segundos"; cat /tmp/boleteria-failover.json
