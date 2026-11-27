
# Laboratorio 10 - Integración Backend Seguro (APF2)

## 1. Descripción del proyecto

API REST para la gestión de productos e inventario, desarrollada con Spring Boot, MySQL y Spring Security.

El proyecto integra los conocimientos de los laboratorios anteriores, incorporando autenticación JWT, autorización por roles, consultas JPQL, transacciones, manejo de excepciones y pruebas automatizadas.

## 2. Tecnologías utilizadas

- Java 21
- Spring Boot 4.1.1
- Spring Security
- Spring Data JPA
- JWT
- MySQL
- Maven
- JUnit 5
- Mockito
- Postman
- Visual Studio Code
- Git y GitHub

## 3. Arquitectura del proyecto

El proyecto utiliza una arquitectura por capas:

- Controller: exposición de endpoints REST.
- Service: lógica de negocio y transacciones.
- Repository: acceso a la base de datos.
- Model: entidades JPA.
- DTO: transferencia de datos.
- Security: autenticación y autorización.
- Config: configuración e inicialización.
- Exception: manejo centralizado de errores.

Flujo de la aplicación:

Cliente → Spring Security → Controller → Service → Repository → MySQL

## 4. Configuración de la base de datos

Crear la base de datos en MySQL:

```sql
CREATE DATABASE IF NOT EXISTS productos_db;
USE productos_db;
```

Hibernate se encarga de crear y actualizar las tablas mediante la configuración ddl-auto=update.

## 5. Variables de entorno

Por seguridad, las contraseñas y la clave JWT no se almacenan directamente en el código fuente actual.

Configurar las siguientes variables desde PowerShell:

```powershell
$env:DB_PASSWORD = Read-Host "Contraseña MySQL"

$env:APP_USER_PASSWORD = Read-Host "Contraseña inicial USER"

$env:APP_ADMIN_PASSWORD = Read-Host "Contraseña inicial ADMIN"

$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$env:JWT_SECRET_BASE64 = [Convert]::ToBase64String($bytes)
```

Las contraseñas iniciales se utilizan cuando los usuarios todavía no existen en la base de datos.

## 6. Ejecución del proyecto

Compilar:

```powershell
.\mvnw.cmd clean compile
```

Ejecutar:

```powershell
.\mvnw.cmd spring-boot:run
```

Servidor local:

http://localhost:8080

## 7. Autenticación y autorización

La aplicación utiliza tokens JWT con una duración de 30 minutos.

Roles disponibles:

- ROLE_USER: consulta de productos.
- ROLE_ADMIN: administración de productos, inventario y reportes.

Las solicitudes protegidas deben incluir la cabecera:

Authorization: Bearer TOKEN_JWT

## 8. Endpoints principales

| Método | Endpoint | Acceso |
|---|---|---|
| POST | /api/auth/login | Público |
| GET | /api/auth/me | Autenticado |
| GET | /api/productos | USER / ADMIN |
| GET | /api/productos/{id} | USER / ADMIN |
| GET | /api/productos/rango?min=&max= | USER / ADMIN |
| POST | /api/productos | ADMIN |
| PUT | /api/productos/{id} | ADMIN |
| PATCH | /api/productos/{id}/precio | ADMIN |
| DELETE | /api/productos/{id} | ADMIN |
| POST | /api/inventario/{id}/salida?cantidad=2 | ADMIN |
| GET | /api/admin/resumen | ADMIN |
| GET | /api/admin/reporte | ADMIN |

## 9. Consultas JPQL

Se implementaron consultas para:

- Buscar productos por nombre.
- Consultar productos por rango de precios.
- Consultar productos con stock bajo.

También se incorporaron métodos derivados de Spring Data JPA para consultar categorías y contabilizar productos.

## 10. Gestión de inventario y transacciones

El registro de una salida de inventario realiza las siguientes operaciones:

1. Buscar el producto.
2. Validar la cantidad solicitada.
3. Verificar la disponibilidad de stock.
4. Descontar la cantidad del inventario.
5. Registrar el movimiento de salida.
6. Confirmar la transacción.

Se utiliza la anotación @Transactional para garantizar la integridad de los datos.

También se verificó el rollback mediante una simulación de error, comprobando que el stock y los movimientos permanecen sin cambios.

## 11. Manejo de excepciones

| Código HTTP | Descripción |
|---|---|
| 200 | Operación exitosa |
| 201 | Recurso creado |
| 400 | Solicitud incorrecta |
| 401 | Usuario no autenticado |
| 403 | Acceso no autorizado |
| 404 | Recurso no encontrado |
| 409 | Stock insuficiente |
| 500 | Error interno del servidor |

## 12. Resumen administrativo

El endpoint GET /api/admin/resumen permite consultar:

- Total de productos.
- Productos con stock menor a 5 unidades.
- Total de movimientos de inventario.
- Estado operativo del backend.

Este endpoint es exclusivo del rol ADMIN.

## 13. Pruebas automatizadas

Ejecutar:

```powershell
.\mvnw.cmd clean test
```

Resultado obtenido:

- Pruebas ejecutadas: 13
- Fallos: 0
- Errores: 0
- Pruebas omitidas: 0
- BUILD SUCCESS

Se realizaron pruebas de servicios, controladores, JWT, contexto de Spring Boot y autorización por roles.

## 14. Empaquetado

Ejecutar:

```powershell
.\mvnw.cmd clean package
```

Archivo generado:

target/productos-api-0.0.1-SNAPSHOT.jar

Resultado: BUILD SUCCESS.

## 15. Validaciones realizadas

- Autenticación de usuarios ADMIN y USER.
- Acceso a endpoints protegidos.
- Restricción de operaciones administrativas.
- Consulta de productos mediante JPQL.
- Registro de movimientos de inventario.
- Validación de stock insuficiente.
- Verificación de rollback.
- Ejecución de pruebas automatizadas.
- Compilación y empaquetado Maven.

## 16. Conclusión

El Laboratorio 10 permitió integrar los componentes del backend en una API REST segura, aplicando buenas prácticas de arquitectura, autenticación, autorización, persistencia de datos y gestión de transacciones.

Las 13 pruebas automatizadas aprobadas y el empaquetado exitoso respaldan el funcionamiento de los componentes evaluados.

## 17. Autor

- Estudiante: Minyer Cornelio Maturrano
- Universidad: Universidad Tecnológica del Perú (UTP)
- Curso: Desarrollo Web
- Laboratorio: 10
- Año: 2026
