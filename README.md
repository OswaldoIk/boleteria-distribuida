# Boletería distribuida

Aplicación de demostración para reservar, confirmar y cancelar boletas. Usa Spring Boot, PostgreSQL 16 con replicación y una interfaz web que se actualiza en vivo.

## Guía rápida para descargar, iniciar y usar

VS Code no es necesario para ejecutar el programa. Los comandos se escriben en PowerShell y la aplicación se usa desde el navegador.

### 1. Descarga el proyecto

Descarga el ZIP del repositorio desde el sitio donde te compartieron el proyecto y descomprímelo. Si está publicado en GitHub, usa **Code > Download ZIP**. También puedes clonarlo con Git si tienes la dirección del repositorio:

```powershell
git clone <DIRECCION_DEL_REPOSITORIO>
```

Al terminar, debe existir una carpeta con `docker-compose.yml`, `.env.example` y las carpetas `backend`, `db` y `scripts`.

### 2. Instala e inicia Docker Desktop

En Windows instala Docker Desktop y habilita el motor WSL 2 si el instalador lo solicita. Abre Docker Desktop y espera a que indique que Docker está ejecutándose. No basta con tener Docker instalado: el motor debe estar iniciado.

Abre una ventana nueva de PowerShell y comprueba que estos dos comandos respondan:

```powershell
docker --version
docker compose version
```

No necesitas instalar Java, Maven, PostgreSQL ni VS Code para iniciar la aplicación con Docker; la construcción descarga las dependencias necesarias.

### 3. Abre PowerShell en la carpeta del proyecto

En el Explorador de archivos, abre la carpeta descomprimida, haz clic en la barra de dirección, escribe `powershell` y presiona Enter. También puedes navegar desde PowerShell:

```powershell
cd "C:\ruta\a\boleteria-distribuida"
```

Comprueba que estás en el lugar correcto:

```powershell
Get-ChildItem docker-compose.yml, .env.example
```

### 4. Crea el archivo de configuración local

Compose busca un archivo llamado `.env` en la misma carpeta que `docker-compose.yml`. Créalo y ábrelo así:

```powershell
Copy-Item .env.example .env
notepad .env
```

Guarda los cambios en Notepad y cierra la ventana. Asegúrate de que el archivo se llame `.env`, no `.env.txt`. No compartas ese archivo: contiene las claves de la base de datos. Para usar la pantalla de reservas basta con configurar las variables del ejemplo; para acceder al estado protegido del clúster, cambia `APP_DEVOPS_PASSWORD_HASH` por un hash BCrypt válido.

### 5. Construye e inicia el programa

En esa misma ventana de PowerShell, ejecuta:

```powershell
docker compose up -d --build
```

La primera vez puede tardar varios minutos: Docker descarga las imágenes base, instala PostgreSQL y Maven descarga las dependencias Java durante la construcción. Espera a que el comando termine y vuelva a mostrar el prompt `PS ...>`.

### 6. Espera y comprueba que los servicios estén listos

```powershell
docker compose ps
```

Los servicios `nodo_centos_primary`, `nodo_centos_secondary` y `backend` deben mostrar `healthy`. Si alguno sigue como `starting`, espera unos segundos y vuelve a ejecutar `docker compose ps`.

### 7. Abre y prueba la interfaz

Abre Edge o Chrome y visita:

**http://localhost:8080**

Debes ver la lista de localidades y el indicador de WebSocket. Deja el usuario de demostración `usr_demo` o escribe otro. Pulsa **Reservar** en una localidad; para aforo general la página pregunta cuántas boletas quieres. La reserva aparecerá en **Mis reservas**, donde puedes pulsar **Confirmar** o **Cancelar**.

Para observar la actualización en tiempo real, abre `http://localhost:8080` en dos pestañas. Reserva desde una y mira cómo cambia la disponibilidad en la otra.

### 8. Detén o vuelve a iniciar el programa

Para detener los contenedores y conservar los datos:

```powershell
docker compose down
```

Para volver a iniciarlos después:

```powershell
docker compose up -d
```

Para borrar también las bases de datos y todas las reservas guardadas (acción irreversible):

```powershell
docker compose down -v
```

## Solución de problemas al iniciar

- **`docker` no se reconoce:** instala Docker Desktop, cierra y abre PowerShell de nuevo.
- **Error de conexión a `docker_engine` o `docker API`:** abre Docker Desktop y espera a que el motor termine de iniciar; después repite el comando.
- **Compose dice que faltan variables** como `POSTGRES_DB` o `APP_DEVOPS_PASSWORD_HASH`: crea `.env` con `Copy-Item .env.example .env` en la carpeta del Compose.
- **Un servicio no aparece `healthy`:** revisa sus mensajes con `docker compose logs --tail 100`. Comparte el error completo si necesitas ayuda.
- **La página no abre:** comprueba que `backend` esté `healthy` con `docker compose ps` y vuelve a intentar `http://localhost:8080`.

## Servicios y puertos

| Servicio | Dirección local |
|---|---|
| Aplicación web y API | `http://localhost:8080` |
| PostgreSQL primario | `localhost:5432` |
| PostgreSQL réplica | `localhost:5433` |

## Verificación de replicación

Desde PowerShell, con los contenedores activos:

```powershell
docker exec nodo_centos_primary sh -lc 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "select application_name,state,sync_state from pg_stat_replication;"'
docker exec nodo_centos_primary sh -lc 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "select pg_is_in_recovery();"'
docker exec nodo_centos_primary sh -lc 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "show cluster_name;"'
docker exec nodo_centos_secondary sh -lc 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "select pg_is_in_recovery();"'
docker exec nodo_centos_secondary sh -lc 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "show cluster_name;"'
```

En el primario, `pg_is_in_recovery()` debe ser `f`; en la réplica debe ser `t`. La consulta a `pg_stat_replication` en el primario debe mostrar la réplica conectada.

## API REST

```text
GET    /api/v1/localidades
GET    /api/v1/localidades/{id}
POST   /api/v1/reservas
POST   /api/v1/reservas/{id}/confirmar
DELETE /api/v1/reservas/{id}
GET    /api/v1/cluster/estado  (requiere rol DEVOPS por HTTP Basic)
```

Ejemplo de creación desde PowerShell:

```powershell
curl.exe -X POST http://localhost:8080/api/v1/reservas -H "Content-Type: application/json" -d '{"localidadId":"UUID_DE_LA_LOCALIDAD","usuarioId":"usr_demo","cantidad":1}'
```

Para consultar el estado del clúster, configura un usuario y hash BCrypt en `.env` y autentícate con HTTP Basic usando la contraseña correspondiente al hash.

## Pruebas, carga y failover

Para ejecutar pruebas automatizadas necesitas Java 21, Maven 3.9 y Docker accesible para Testcontainers. Desde `backend/` ejecuta `mvn test`. Para medir concurrencia, usa Bash, `curl` y Docker Compose:

```bash
bash scripts/test-concurrencia.sh UUID_LOCALIDAD 200 32
```

Abre `jmeter/plan-reservas.jmx` con JMeter. Para hacer el failover de laboratorio, define `LOCALIDAD_ID` con una localidad disponible y ejecuta `bash scripts/failover.sh`. Para reintegrar el nodo anterior como réplica usa `bash scripts/reintegrar-nodo.sh nodo_centos_secondary nodo_centos_primary`.

## Limitaciones conocidas

- La replicación es asíncrona; un failover puede perder las últimas transacciones aún no replicadas.
- La promoción y reintegración son manuales; Compose no elige un primario automáticamente.
- Los compradores no se autentican; `usuarioId` solo identifica una reserva.
- El broker WebSocket es simple y vive en memoria del proceso.
- La creación reintentada no es idempotente; el cliente debe evitar repetir una solicitud tras una respuesta ambigua.
