# Decisiones de implementación y verificación

## Verificación pendiente por herramientas ausentes

El workspace de esta entrega no tenía `docker`, `docker compose` ni `mvn` en PATH. Por eso no se pudieron correr las verificaciones de Compose, compilación Maven, integración PostgreSQL, concurrencia, failover ni navegación de dos pestañas. El JDK detectado fue Java 27; la imagen de build declara Java 21 según el stack requerido. Sí se validó que `backend/pom.xml` y `jmeter/plan-reservas.jmx` son XML bien formados y que `app.js` pasa `node --check`. Esas comprobaciones no sustituyen el arranque real de los servicios ni la ejecución de JUnit/JMeter. La ausencia de herramientas es un bloqueo del entorno, no evidencia de que los comandos pasen.

## Corrección de configuración de Hikari

Se enlazan `spring.datasource.write` y `spring.datasource.read` directamente en instancias Hikari. Usar `DataSourceProperties` en este caso podía dejar sin enlazar `jdbc-url`, pues el prefijo de las propiedades específicas de Hikari usa ese nombre. El selector por transacción queda en Spring mediante `AbstractRoutingDataSource`, con conexión diferida para conocer el estado read-only al abrirla.

## Decisiones operativas

- El rol de aplicación recibe `pg_monitor` para consultar estado de replicación; el usuario operativo DEVOPS de la API es independiente y su contraseña se representa con BCrypt.
- Los scripts requieren Bash y Docker Compose. La promoción sigue siendo explícita para prevenir split-brain; ambos servicios de base arrancan con `restart: "no"`.
- El perfil de pruebas usa Testcontainers como la opción preferida especificada, pero su ejecución depende de Docker y de acceso a las imágenes.
- El plan JMeter usa una aserción de respuesta para aceptar 201 y 409, los resultados normales de un caso de carga de creación de reserva.
