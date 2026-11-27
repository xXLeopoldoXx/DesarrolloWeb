# Laboratorio 15 · Tienda UTP · Anelhi Marco

Tienda UTP permite consultar productos y filtrar por precio. Las cuentas administradoras también pueden crear productos, registrar salidas de stock y consultar indicadores. El laboratorio usa PostgreSQL para las comprobaciones de persistencia y transacciones de la guía.

El backend parte de la entrega de [Maria Ramos del Laboratorio 14](../../Laboratorio14/Maria_Ramos). La configuración de herramientas de Angular 22 toma como referencia la [entrega de Brito Ruiz](../../Laboratorio14/Brito_Ruiz/frontend).

## Arquitectura

Angular envía solicitudes HTTP al prefijo `/api`. El proxy de desarrollo las reenvía a Spring Boot, que autentica con JWT y aplica los permisos antes de llamar a los servicios. JPA accede a PostgreSQL y ejecuta la consulta de precios mediante JPQL. El token vive en memoria y la recarga completa pide volver a iniciar sesión.

Entorno de verificación: Angular 22.2.6, Angular CLI 22.2.7, TypeScript 6.0.3, Node.js 24.22.0, Vitest 4.1.12, Java 25.0.4, Maven Wrapper, Spring Boot 4.1.1 y PostgreSQL 18.6.

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

### Ejecutar Angular

En otra terminal de VS Code, desde la carpeta de esta entrega, ejecuta:

```powershell
Set-Location frontend
npm.cmd ci
npm.cmd start
```

Abre [Tienda UTP](http://localhost:4200). Usa `usuario` para consultar y `admin` para administrar, con las contraseñas ficticias que configuraste en el perfil `demo`. Si la API se ejecuta en un puerto distinto a `8080`, ajusta el destino de `frontend/proxy.conf.json` antes de iniciar Angular.

## Rutas de la API

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

Ejemplo de cuerpo para `POST /api/productos`.

```json
{"nombre":"Monitor de laboratorio","categoria":"Tecnología","precio":450,"stock":10}
```

Ejemplo de respuesta posterior a una salida de 2 unidades.

```json
{"id":1,"nombre":"Monitor de laboratorio","categoria":"Tecnología","precio":450,"stock":8}
```

## Pruebas y compilaciones

Desde la carpeta del laboratorio, ejecuta Maven. `package` también pasa por la fase `test`.

```powershell
& './mvnw.cmd' clean package
```

Desde `frontend`, ejecuta las pruebas de Angular y genera la compilación de producción.

```powershell
npm.cmd test -- --watch=false
npm.cmd run build
```

La suite habitual de Maven usa una base en memoria. Las verificaciones de persistencia en PostgreSQL se ejecutan en una base dedicada con datos ficticios.

La [matriz de pruebas y capturas](evidencias/README.md) detalla los resultados observados. El entorno está pensado para ejecución local y las cuentas del perfil `demo` son de laboratorio.

## Preguntas de discusión

1. **¿Dónde se aplica el permiso real?** El guard orienta la navegación de Angular. Spring Security evalúa cada solicitud, incluso si llega desde Postman.
2. **¿Qué se revisa ante un 403?** Primero el método y la URL en Network. Luego el perfil de `/api/auth/me`, los roles y la regla de Spring Security.
3. **¿Por qué puede variar el stock?** Otro administrador puede registrar un movimiento mientras el formulario está abierto. El servidor vuelve a leer el stock con un bloqueo de escritura.
4. **¿Qué significan 0, 401 y 409?** Angular usa 0 si no recibe respuesta. Un 401 indica que la autenticación falta o fue rechazada. Un 409 indica un conflicto con el estado actual, como stock insuficiente.
5. **¿Dónde se adapta un cambio de tablas?** El mapeo JPA y los repositorios encapsulan la persistencia. Las pantallas pueden seguir usando el mismo contrato HTTP.
6. **¿Qué prueba `HttpTestingController`?** Método, URL, parámetros, cabeceras y manejo de respuestas simuladas. Las llamadas reales y la persistencia se prueban por separado.
7. **¿Para qué probar un fallo transaccional?** Para comprobar que un error posterior a la escritura revierte tanto la cantidad del producto como el movimiento.
8. **¿Cómo se reproduce la entrega?** A partir del código, archivos de dependencias, variables documentadas, cuentas ficticias, comandos de compilación y evidencias de los resultados.
