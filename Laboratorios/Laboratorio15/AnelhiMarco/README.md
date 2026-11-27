# Laboratorio 15 · Tienda UTP · Anelhi Marco

La base del backend proviene de la entrega de [Maria Ramos del Laboratorio 14](../../Laboratorio14/Maria_Ramos). Este laboratorio conserva PostgreSQL por indicación de Anelhi y aplica en PostgreSQL las comprobaciones de persistencia y transacciones planteadas en la guía.

**Estado de la entrega:** el backend y sus pruebas de integración están disponibles. El formulario Angular y las evidencias de la integración completa quedan para la siguiente etapa.

## Ejecutar el backend local

Se requiere Java 25 y PostgreSQL. El Maven Wrapper del proyecto proporciona Maven.

En pgAdmin, crea una base dedicada llamada `tienda_lab15_anelhi` y asígnale un usuario local con permisos sobre esa base. También puedes crearla en la herramienta de consultas conectado a la base administrativa:

```sql
CREATE DATABASE tienda_lab15_anelhi;
```

Desde la terminal PowerShell de VS Code, situada en esta carpeta, configura las variables solo para esa sesión. Usa contraseñas de laboratorio distintas a tus credenciales personales para las dos cuentas de demostración.

```powershell
$env:LAB15_DB_USER = Read-Host 'Usuario de PostgreSQL'
$env:LAB15_DB_PASSWORD = Read-Host 'Contraseña de PostgreSQL' -MaskInput
$env:LAB15_JWT_SECRET_BASE64 = [Convert]::ToBase64String(
    [System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32)
)
$env:LAB15_DEMO_USER_PASSWORD = Read-Host 'Contraseña de la cuenta ficticia usuario' -MaskInput
$env:LAB15_DEMO_ADMIN_PASSWORD = Read-Host 'Contraseña de la cuenta ficticia admin' -MaskInput
& './mvnw.cmd' spring-boot:run '-Dspring-boot.run.profiles=demo'
```

Spring Boot escucha en `127.0.0.1:8080` y conecta a PostgreSQL en `127.0.0.1:5432`. Puedes configurar `LAB15_API_PORT` y `LAB15_DB_PORT` si usas otros puertos. Hibernate crea o actualiza las tablas dentro de la base dedicada. El perfil `demo` crea las cuentas ficticias `usuario` y `admin` si todavía no existen. Cada ejecución con una clave JWT nueva requiere iniciar sesión de nuevo.

## Rutas que usará Angular

| Operación | Ruta | Acceso |
| --- | --- | --- |
| Iniciar sesión | `POST /api/auth/login` | Público |
| Consultar perfil | `GET /api/auth/me` | Autenticado |
| Consultar catálogo y precios | `GET /api/productos` y `GET /api/productos/precio?min=10&max=100` | USER y ADMIN |
| Consultar producto | `GET /api/productos/{id}` | USER y ADMIN |
| Crear producto | `POST /api/productos` | ADMIN |
| Registrar salida | `POST /api/inventario/{id}/salida?cantidad=3` | ADMIN |
| Consultar indicadores | `GET /api/admin/resumen` | ADMIN |

La salida devuelve el producto actualizado. Spring Security valida el token y el rol. Dentro de la transacción, el servicio bloquea la fila del producto para validar el stock y registrar el movimiento. Si falla la transacción, se revierte el cambio completo.

## Pruebas del backend

```powershell
& './mvnw.cmd' test
```

La suite completa pasó 16 pruebas. Usa H2 en memoria para comprobar la API, los permisos y el rollback desde Maven sin requerir credenciales de PostgreSQL.

En esta etapa también se ejecutaron seis pruebas de integración contra un clúster temporal de PostgreSQL 18.6. Pasaron las comprobaciones de autorización JWT, validación de cantidad, persistencia de la salida y rollback. La instancia temporal se detuvo al terminar.
