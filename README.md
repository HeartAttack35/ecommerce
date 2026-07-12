# Ecommerce — Arquitectura de Microservicios

Plataforma de comercio electrónico construida sobre una arquitectura de microservicios con Spring Boot 4, Spring Cloud y MySQL. Cada dominio de negocio vive en su propio servicio independiente, con base de datos propia, seguridad JWT y documentación automática con Swagger.

---

## Tabla de contenidos

1. [Arquitectura general](#arquitectura-general)
2. [Microservicios](#microservicios)
3. [Tecnologías utilizadas](#tecnologías-utilizadas)
4. [Seguridad Zero Trust](#seguridad-zero-trust)
5. [Comunicación entre servicios](#comunicación-entre-servicios)
6. [Variables de entorno](#variables-de-entorno)
7. [Requisitos previos](#requisitos-previos)
8. [Compilar los JARs antes de levantar](#compilar-los-jars-antes-de-levantar)
9. [Despliegue local con Docker Compose](#despliegue-local-con-docker-compose)
10. [Documentación de la API (Swagger)](#documentación-de-la-api-swagger)
11. [Pruebas unitarias y cobertura](#pruebas-unitarias-y-cobertura)
12. [Despliegue en la Nube](#despliegue-en-la-nube)

---

## Arquitectura general

```
Cliente HTTP
     │
     ▼
┌─────────────┐
│  ms-gateway │  :8080  — Punto de entrada único, enrutamiento dinámico
└──────┬──────┘
       │ lb://
       ▼
┌─────────────┐        ┌──────────────────────────────────────────────┐
│  ms-eureka  │ :8761  │  Registro y descubrimiento de servicios       │
└─────────────┘        └──────────────────────────────────────────────┘
       │ se registran
       ├── ms-auth         :8081   BD: mysql-auth        (3307)
       ├── ms-cliente       :8082   BD: mysql-cliente     (3309)
       ├── ms-producto      :8083   BD: mysql-producto    (3308)
       ├── ms-inventario    :8084   BD: mysql-inventario  (3312)
       ├── ms-pago          :8085   BD: mysql-pago        (3311)
       ├── ms-envio         :8086   BD: mysql-envio       (3313)
       ├── ms-notificacion  :8087   BD: mysql-notificacion(3314)
       ├── ms-pedido        :8088   BD: mysql-pedido      (3310)
       │                       └── Feign ──► ms-inventario
       ├── ms-carrito       :8089   BD: mysql-carrito     (3315)
       ├── ms-promocion     :8090   BD: mysql-promocion   (3316)
       └── ms-resena        :8091   BD: mysql-resena      (3317)
```

Todos los servicios comparten la red interna `backend-network` de Docker. Ningún microservicio de negocio es accesible desde el exterior sin pasar por el gateway.

**Rutas del Gateway:**

| Ruta pública           | Microservicio destino |
|------------------------|-----------------------|
| `/api/auth/**`         | ms-auth               |
| `/api/clientes/**`     | ms-cliente            |
| `/api/productos/**`    | ms-producto           |
| `/api/inventario/**`   | ms-inventario         |
| `/api/pedidos/**`      | ms-pedido             |
| `/api/pagos/**`        | ms-pago               |
| `/api/envios/**`       | ms-envio              |
| `/api/notificaciones/**` | ms-notificacion     |
| `/api/carrito/**`      | ms-carrito            |
| `/api/promociones/**`  | ms-promocion          |
| `/api/resenas/**`      | ms-resena             |

---

## Microservicios

| Servicio | Puerto | Build | Responsabilidad |
|---|---|---|---|
| **ms-eureka** | 8761 | Maven | Servidor de descubrimiento (Eureka Server) |
| **ms-gateway** | 8080 | Maven | API Gateway — enrutamiento y balanceo de carga |
| **ms-auth** | 8081 | Maven | Autenticación y emisión de tokens JWT |
| **ms-cliente** | 8082 | Maven | Gestión de clientes registrados |
| **ms-producto** | 8083 | Maven | Catálogo de productos |
| **ms-inventario** | 8084 | Maven | Control de stock y disponibilidad |
| **ms-pago** | 8085 | Maven | Procesamiento de pagos |
| **ms-envio** | 8086 | Maven | Despacho y seguimiento de envíos |
| **ms-notificacion** | 8087 | Maven | Envío de notificaciones (EMAIL, SMS, PUSH) |
| **ms-pedido** | 8088 | Maven | Creación y seguimiento de pedidos |
| **ms-carrito** | 8089 | Gradle | Carrito de compras: ítems, confirmación |
| **ms-promocion** | 8090 | Gradle | Códigos de descuento y validación de vigencia |
| **ms-resena** | 8091 | Gradle | Reseñas y puntuaciones de productos |

---

## Tecnologías utilizadas

- **Java 21** y **Spring Boot 4.1**
- **Spring Cloud 2025.1.2** — Eureka, Gateway, OpenFeign
- **Spring Security** + **JJWT 0.12.3** — autenticación stateless con JWT
- **Spring Data JPA** + **Hibernate** — persistencia
- **Spring HATEOAS** — respuestas hipermedia (nivel 3 REST)
- **springdoc-openapi 2.6.0** — documentación automática Swagger/OpenAPI
- **Lombok** — reducción de boilerplate
- **MySQL 8** — base de datos por microservicio (13 instancias)
- **Docker** + **Docker Compose** — contenedorización
- **JUnit 5** + **Mockito** + **JaCoCo** — pruebas unitarias y cobertura

---

## Seguridad Zero Trust

Ningún microservicio confía en otro por defecto. Cada request interna debe incluir un JWT válido.

**Flujo de autenticación:**

```
1. POST /api/auth/login  →  ms-auth emite un JWT firmado con jwt.secret
2. El cliente incluye el token en cada request:
   Authorization: Bearer <token>
3. El JwtFilter de cada microservicio valida la firma antes de procesar
4. ms-pedido → ms-inventario (Feign): el FeignClientInterceptor
   propaga automáticamente el header Authorization
```

El secreto compartido `JWT_SECRET` es la misma cadena en todos los servicios, inyectada como variable de entorno.

---

## Comunicación entre servicios

`ms-pedido` consulta el stock de `ms-inventario` de forma síncrona antes de crear un pedido, usando **OpenFeign**:

```java
@FeignClient(name = "ms-inventario")
public interface InventarioClient {
    @GetMapping("/inventario/producto/{productoId}/stock")
    Integer obtenerStockPorProducto(@PathVariable("productoId") Long productoId);
}
```

Si el stock es insuficiente, el pedido es rechazado con `400 Bad Request` antes de persistir nada.

---

## Variables de entorno

Cada microservicio de negocio acepta las siguientes variables. Todos tienen valores por defecto para desarrollo local.

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `PORT` | Puerto del servidor | según el servicio |
| `DB_HOST` | Host de MySQL | `localhost` |
| `DB_PORT` | Puerto de MySQL | `3308` |
| `DB_NAME` | Nombre de la base de datos | `db_<servicio>` |
| `DB_USER` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contraseña de MySQL | `root` |
| `JWT_SECRET` | Clave secreta para firmar JWT | `ecommerce_secreto_super_seguro_1234567890` |
| `EUREKA_URL` | URL del servidor Eureka | `http://localhost:8761/eureka/` |

> **Producción:** reemplazar `JWT_SECRET` por una cadena aleatoria de al menos 32 caracteres y gestionar las contraseñas de BD con un gestor de secretos.

---

## Requisitos previos

- **Docker** >= 24 y **Docker Compose** v2
- **Java 21** y **Maven** (para compilar los JARs de los servicios Maven)
- **Gradle** (los servicios Gradle compilan dentro del contenedor Docker)
- Git

```bash
# Verificar versiones
docker --version
docker compose version
java --version
```

---

## Compilar los JARs antes de levantar

El proyecto tiene dos tipos de servicios según su herramienta de build:

**Servicios Maven** (necesitan JAR pre-compilado antes de `docker compose up`):

Sus `Dockerfile` copian el JAR desde `target/`, por lo que hay que compilarlos primero:

```bash
# Desde la raíz del proyecto
for ms in ms-eureka ms-gateway ms-auth ms-cliente ms-producto \
           ms-inventario ms-pedido ms-pago ms-envio ms-notificacion; do
  echo "▶ Compilando $ms..."
  (cd $ms && ./mvnw clean package -DskipTests -q)
done
```

O uno a uno:

```bash
cd ms-auth && ./mvnw clean package -DskipTests && cd ..
```

**Servicios Gradle** (ms-carrito, ms-promocion, ms-resena):

Usan un `Dockerfile` multi-stage que **compila dentro del contenedor**. No requieren pasos previos — `docker compose up --build` se encarga de todo.

---

## Despliegue local con Docker Compose

Con los JARs Maven compilados, levantar toda la plataforma es un solo comando:

```bash
# Construir imágenes y levantar todos los contenedores en segundo plano
docker compose up --build -d
```

**Orden de arranque** (gestionado automáticamente por `depends_on`):
1. 13 instancias de MySQL
2. `ms-eureka`
3. Microservicios de negocio (en paralelo)
4. `ms-gateway`
5. `ms-pedido` (último, depende de `ms-inventario`)

**Comandos útiles:**

```bash
# Ver estado de todos los contenedores
docker compose ps

# Ver logs de un servicio específico
docker compose logs -f ms-carrito

# Detener y eliminar contenedores (conserva los volúmenes de BD)
docker compose down

# Detener y eliminar también los volúmenes (borra los datos)
docker compose down -v

# Reiniciar un servicio individual sin afectar al resto
docker compose restart ms-promocion
```

**Verificar que Eureka registró los servicios:**

```
http://localhost:8761
```

---

## Documentación de la API (Swagger)

Cada microservicio expone su propia UI de Swagger. Las rutas `/swagger-ui/**` y `/v3/api-docs/**` están abiertas sin necesidad de token.

| Servicio | URL Swagger local |
|---|---|
| ms-auth | http://localhost:8081/swagger-ui/index.html |
| ms-cliente | http://localhost:8082/swagger-ui/index.html |
| ms-producto | http://localhost:8083/swagger-ui/index.html |
| ms-inventario | http://localhost:8084/swagger-ui/index.html |
| ms-pago | http://localhost:8085/swagger-ui/index.html |
| ms-envio | http://localhost:8086/swagger-ui/index.html |
| ms-notificacion | http://localhost:8087/swagger-ui/index.html |
| ms-pedido | http://localhost:8088/swagger-ui/index.html |
| ms-carrito | http://localhost:8089/swagger-ui/index.html |
| ms-promocion | http://localhost:8090/swagger-ui/index.html |
| ms-resena | http://localhost:8091/swagger-ui/index.html |

---

## Pruebas unitarias y cobertura

Las pruebas usan **JUnit 5** con **Mockito** y están en la capa de servicio de cada microservicio. No requieren base de datos ni contexto de Spring.

```bash
# Servicios Maven — ejecutar pruebas y generar reporte JaCoCo
cd ms-pedido
./mvnw clean test
xdg-open target/site/jacoco/index.html

# Servicios Gradle — ejecutar pruebas
cd ms-carrito
./gradlew test --no-daemon
xdg-open build/reports/tests/test/index.html
```

**Casos cubiertos por microservicio:**

| Servicio | Tests | Happy Path | Error Path |
|---|---|---|---|
| ms-pedido | 7 | crear con stock, listar, buscar, actualizar estado, eliminar | stock insuficiente, not found |
| ms-cliente | 7 | crear, listar, buscar, actualizar, eliminar | email duplicado, not found |
| ms-producto | 6 | crear, listar, buscar, actualizar, eliminar | not found |
| ms-inventario | 7 | crear, listar, buscar por producto, actualizar, eliminar | producto duplicado, not found |
| ms-pago | 5 | crear, listar, actualizar estado, eliminar | not found |
| ms-envio | 5 | crear, listar, actualizar estado, eliminar | not found |
| ms-notificacion | 5 | enviar, listar, por cliente, eliminar | not found |
| ms-carrito | 6 | crear, agregar ítems, confirmar, listar, buscar, eliminar | carrito duplicado, carrito vacío, not found |
| ms-promocion | 6 | crear, validar código, listar, actualizar, desactivar, eliminar | código expirado, código duplicado, not found |
| ms-resena | 6 | crear, listar por producto, listar por cliente, actualizar, ocultar, eliminar | reseña duplicada, not found |

---

## Despliegue en la Nube

El proyecto se puede desplegar en [Railway](https://railway.app) de forma individual por microservicio. Railway detecta el `Dockerfile` automáticamente y provee bases de datos MySQL como plugins.

### Pasos para desplegar un microservicio

**1. Crear el proyecto en Railway**

- Ir a [railway.app](https://railway.app) e iniciar sesión con GitHub.
- Crear un nuevo proyecto con **"Deploy from GitHub repo"** y seleccionar el repositorio.
- Como el repositorio tiene múltiples servicios en subcarpetas, usar **"Add Service → GitHub Repo"** y configurar el **Root Directory** apuntando a la carpeta del microservicio (ej. `/ms-carrito`).

**2. Agregar la base de datos MySQL**

- Dentro del mismo proyecto, hacer clic en **"Add Service → Database → MySQL"**.
- Railway provee automáticamente las variables `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_USER`, `MYSQL_PASSWORD` y `MYSQL_DATABASE`.

**3. Configurar las variables de entorno**

En la pestaña **Variables** del servicio Spring Boot, agregar:

| Variable | Valor en Railway |
|---|---|
| `DB_HOST` | `${{MySQL.MYSQL_HOST}}` |
| `DB_PORT` | `${{MySQL.MYSQL_PORT}}` |
| `DB_NAME` | `${{MySQL.MYSQL_DATABASE}}` |
| `DB_USER` | `${{MySQL.MYSQL_USER}}` |
| `DB_PASSWORD` | `${{MySQL.MYSQL_PASSWORD}}` |
| `JWT_SECRET` | _(generar una cadena segura de 32+ caracteres)_ |
| `EUREKA_URL` | URL pública del servicio `ms-eureka` en Railway |

> Railway usa la sintaxis `${{NombreServicio.VARIABLE}}` para referenciar variables entre servicios del mismo proyecto.

**4. Orden de despliegue recomendado**

```
 1. ms-eureka         → copiar la URL pública generada
 2. ms-auth           → mysql-auth    + EUREKA_URL
 3. ms-producto       → mysql-producto + EUREKA_URL
 4. ms-inventario     → mysql-inventario + EUREKA_URL
 5. ms-cliente        → mysql-cliente + EUREKA_URL
 6. ms-pago           → mysql-pago   + EUREKA_URL
 7. ms-envio          → mysql-envio  + EUREKA_URL
 8. ms-notificacion   → mysql-notificacion + EUREKA_URL
 9. ms-pedido         → mysql-pedido + EUREKA_URL
10. ms-carrito        → mysql-carrito + EUREKA_URL + JWT_SECRET
11. ms-promocion      → mysql-promocion + EUREKA_URL + JWT_SECRET
12. ms-resena         → mysql-resena  + EUREKA_URL + JWT_SECRET
13. ms-gateway        → EUREKA_URL (redesplegar después de agregar los 3 nuevos)
```

**5. Redespliegue del Gateway**

Cada vez que se agregan nuevas rutas al `application.properties` del gateway, se debe forzar un redespliegue en Railway para que el servicio lea la configuración actualizada:

- En el panel de Railway, ir al servicio `ms-gateway`.
- Pestaña **Deployments** → botón **"Redeploy"** en el último despliegue exitoso.

**6. Consideraciones importantes**

- Railway asigna un dominio público a cada servicio (ej. `ms-carrito-production.up.railway.app`). Actualizar `EUREKA_URL` en todos los servicios con la URL real de `ms-eureka`.
- El plan gratuito de Railway tiene límite de horas de ejecución mensuales. Para 13 servicios se recomienda el plan **Hobby** o **Pro**.
- En producción, cambiar `spring.jpa.hibernate.ddl-auto` de `update` a `validate` para evitar modificaciones accidentales al esquema.
- El `JWT_SECRET` debe ser **exactamente el mismo valor** en todos los microservicios desplegados.
- Los servicios Gradle (ms-carrito, ms-promocion, ms-resena) compilan dentro del contenedor Docker, por lo que el build en Railway puede tardar más la primera vez mientras descarga las dependencias.
