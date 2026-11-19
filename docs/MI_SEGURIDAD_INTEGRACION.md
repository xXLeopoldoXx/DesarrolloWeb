# Integración de Mi seguridad

## Borrado de cuenta

`GET /api/mi/cuenta/estado` informa a la pantalla si puede ofrecer la confirmación. `DELETE /api/mi/cuenta` devuelve `204` únicamente al completar la transacción. Ambos endpoints exigen sesión y resuelven la identidad en el servidor. Si falta un requisito, el servidor devuelve `503` antes de ejecutar cualquier limpieza.

`DisponibilidadBorradoCuenta` exige las implementaciones Spring de `planSeguridadService`, `contactoService`, `autoevaluacionService`, `diarioService` y `comunidadService`. Requiere también la autorización de despliegue `app.seguridad.borrado-cuenta.habilitado`, cuyo valor predeterminado es `false`. En la integración revisada, Evaluación, Bienestar y Comunidad todavía no entregan esas implementaciones. El asistente conserva la conversación en memoria de la pantalla y usa limitación temporal en memoria en el backend. Su integración no sustituye ninguna limpieza obligatoria.

`CuentaService` bloquea la fila de la cuenta, ejecuta todas las limpiezas registradas y borra la cuenta dentro de una transacción. Ante un fallo de limpieza devuelve un mensaje genérico y revierte la operación. Las limpiezas deben ser idempotentes, participar en la transacción y respetar el orden de dependencias de cada esquema. Las tres tablas privadas de Mi seguridad conservan el atributo `Long usuarioId` del contrato y tienen una clave foránea restrictiva hacia `usuarios`. Si hay una escritura concurrente, PostgreSQL impide que la eliminación deje registros huérfanos.

Antes de activar el borrado, integrar las otras limpiezas, verificar sus claves foráneas o su estrategia equivalente ante escrituras concurrentes y probar con usuarias ficticias de todos los módulos. Acordar el tratamiento de contenido cruzado de Comunidad, la retención de respaldos, el alcance de la evaluación profesional y el descarte del contador en memoria del asistente, que hoy se indexa por nombre de usuario. En las pruebas de aislamiento se verifica el bloqueo real. Los casos de éxito y rollback prueban la transacción de Mi seguridad sustituyendo únicamente la decisión de disponibilidad. No se registran limpiezas ficticias para los módulos ausentes.

## Sesiones tras eliminar una cuenta

Los JWT nuevos conservan `sub` para las rutas existentes y añaden `uid`, el ID inmutable de la cuenta obtenido del principal que verificó la contraseña. El decodificador valida firma, emisor, vigencia y pertenencia a una cuenta activa. La búsqueda privada usa el ID firmado. Al confirmar el borrado, los tokens de la cuenta eliminada pierden acceso, incluso si el nombre de usuario se registra de nuevo.

Los JWT emitidos antes de incorporar `uid` se rechazan con `401`. El interceptor de Angular limpia esa sesión y la usuaria puede volver a iniciar sesión. Coordinar ese cierre de sesiones al desplegar la actualización. La cadena de rutas y roles de `SecurityConfig` sigue siendo la del contrato.

## Cambios compartidos

| Archivo | Motivo y compatibilidad |
| --- | --- |
| `config/JwtConfig.java` | Añade la validación de cuenta activa al decodificador JWT. Conserva la firma HS256, el emisor y la validación de vigencia. |
| `security/CustomUserDetailsService.java`, `security/TokenService.java` | Vinculan el token con el ID de la cuenta autenticada. `sub` y `roles` conservan su formato para el perfil, Angular y el limitador del asistente. |
| `repository/UsuarioRepository.java`, `service/UsuarioService.java` | Añaden la consulta de identidad activa, el bloqueo transaccional de cuenta y la resolución de propiedad por ID firmado. |
| `frontend/src/styles.scss` | Refuerza el contraste de teal, coral y lila, jerarquía tipográfica, foco, bordes de campo, tamaño de controles y ajuste de tarjetas. Conserva los nombres de clases y variables existentes. |
| `frontend/src/app/core/layout/shell.html`, `shell.scss`, `shell.ts` | Añaden un salto al contenido por teclado, un nombre accesible para el icono de cierre de sesión y ajustes del menú inferior y la cabecera pequeña. Conservan las rutas, enlaces y la salida rápida. |
| `frontend/src/app/shared/salida-rapida/salida-rapida.ts` | Aplica altura táctil y color de acción crítica. Conserva la acción y el texto existentes. |
| `frontend/src/app/features/auth/auth-layout/auth-layout.scss`, `registro/registro.html` | Corrigen contraste y ancho del formulario. El aviso de registro dirige a Privacidad para consultar las opciones disponibles. |
| `frontend/src/app/features/inicio/inicio.scss` | Mejora el contraste del encabezado y permite que las tarjetas se ajusten en pantallas estrechas. |
| `frontend/src/app/features/asistente/asistente.scss` | Ajusta la cabecera y el formulario del chat en pantallas pequeñas sin modificar el contenido ni el comportamiento del asistente. |

El contraste calculado contra blanco es de 5,26:1 para `--teal-600`, 5,39:1 para `--coral-600` y 5,74:1 para `--lila-600`.

## Contratos de formulario

- Las acciones se crean pendientes. El texto admite hasta 240 caracteres y el `PATCH` exige `completada` explícitamente.
- El nombre de un lugar admite 100 caracteres. Referencia y nota son opcionales y admiten hasta 250 y 500 caracteres respectivamente.
- El teléfono de contacto admite de 6 a 15 dígitos con un `+` inicial opcional. Nombre y teléfono son obligatorios. La relación es opcional.
- Las listas se ordenan por ID ascendente. Los campos de texto se recortan y los opcionales en blanco se guardan como nulos.

## Verificación aislada

La prueba de integración PostgreSQL se ejecuta cuando `SPRING_DATASOURCE_URL` apunta a una base exclusiva llamada `riesgo_seguridad_test` en `127.0.0.1`. Usar credenciales temporales y esa misma base para toda la suite Maven, porque la prueba JPA de la base toma el datasource configurado. Para la prueba de navegador, servir la compilación de Angular y el backend contra otra instancia aislada. El asistente puede verificarse con un proveedor HTTP en loopback y una clave ficticia.
