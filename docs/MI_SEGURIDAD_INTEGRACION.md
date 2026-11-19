# Integración de Mi seguridad

## Activación del borrado de cuenta

`DELETE /api/mi/cuenta` conserva el contrato de respuesta `204` cuando el borrado se completa. La extensión de solo lectura `GET /api/mi/cuenta/estado` permite que la pantalla decida si muestra el flujo de confirmación. Ambos endpoints requieren sesión y resuelven la cuenta con `UsuarioService.actual()`. Un intento mientras falten requisitos devuelve `503` antes de ejecutar cualquier limpieza.

`CuentaService` requiere las limpiezas Spring de `planSeguridadService`, `contactoService`, `autoevaluacionService`, `diarioService` y `comunidadService`, además de una implementación de `InvalidacionSesionesCuenta`. Los nombres corresponden a los servicios del contrato. El servicio ejecuta todas las limpiezas registradas, incluida cualquier futura, dentro de la misma transacción que elimina la cuenta. Las limpiezas deben ser idempotentes, participar en la transacción y borrar sus dependencias en el orden que exige su esquema.

La propiedad `app.seguridad.borrado-cuenta.habilitado` se mantiene en `false` hasta que el equipo verifique en conjunto lo siguiente.

- **Cobertura de datos.** Confirmar por módulo qué registros se eliminan. En Comunidad, acordar el tratamiento de comentarios y reportes asociados a una publicación ajena. Definir aparte la retención de respaldos y el alcance del sistema de evaluación profesional.
- **Escrituras concurrentes.** Las tablas nuevas respetan el contrato de `Long usuarioId` y tienen índices por dueña. Antes de activar el borrado, acordar una migración con claves foráneas físicas para los recursos privados y probar que una escritura tardía no deja registros huérfanos. Una clave restrictiva puede hacer fallar y revertir un borrado concurrente. Otra opción es que los escritores y el borrado coordinen el acceso con un bloqueo de la fila de la usuaria.
- **Invalidación real.** Integrar y probar el cambio de identidad del JWT con Leopoldo. Los dobles de la prueba PostgreSQL permiten comprobar la transacción, pero la validación de tokens del aplicativo sigue siendo la de la base.

## Propuesta para invalidar las sesiones

El JWT actual identifica por `username`. La propuesta es incorporar un identificador inmutable de la cuenta al token y validar, en la cadena de seguridad común, que corresponde a una cuenta activa. Conservar temporalmente `sub` para las lecturas actuales del nombre permite migrar el consumidor de identidad por etapas. La validación debe rechazar tokens anteriores que carezcan del identificador antes de habilitar el borrado. Una vez eliminada la fila de la cuenta, todos sus tokens deben rechazarse, aunque otra cuenta vuelva a registrar el mismo nombre.

Si el equipo necesita revocar sesiones sin eliminar la cuenta, puede usar una versión de sesión persistida y comprobarla junto con ese identificador. Ambas opciones requieren cambios coordinados en la emisión y validación del token. `InvalidacionSesionesCuenta` es el punto de integración y esta rama no registra una implementación de producción. Tras la integración hay que verificar un token de otra pestaña, un token anterior con el mismo nombre registrado de nuevo y los endpoints administrativos.

## Contratos de formulario

- Las acciones se crean pendientes. El texto admite hasta 240 caracteres y el `PATCH` exige `completada` explícitamente.
- El nombre de un lugar admite hasta 100 caracteres. Referencia y nota son opcionales y admiten hasta 250 y 500 caracteres respectivamente.
- En contactos, nombre y teléfono son obligatorios. El teléfono admite de 6 a 15 dígitos y un `+` inicial opcional. La relación es opcional y admite hasta 100 caracteres.
- Las listas se ordenan por ID ascendente. Los espacios exteriores se recortan de los campos de texto y los opcionales en blanco se guardan como nulos.

## Pulido visual pendiente de coordinación

El módulo aplica contraste y estilos responsivos en su propia hoja `seguridad.scss` y reutiliza los componentes y clases base. Para extender esas mejoras a toda la aplicación, acordar los tokens de color para acciones primarias y destructivas en `styles.scss`, revisar contraste de etiquetas y errores, y verificar espacio para navegación inferior, asistente y salida rápida en el layout móvil. Revisar esos cambios en todas las pantallas cuando estén los módulos del equipo.

## Verificación con PostgreSQL aislado

La prueba de integración se ejecuta cuando `SPRING_DATASOURCE_URL` apunta a una base exclusiva llamada `riesgo_seguridad_test` en `127.0.0.1`. Configurar un puerto y credenciales temporales en el entorno de pruebas. La suite crea cuentas y registros ficticios. Usar esa misma base aislada para la suite Maven completa, porque la prueba JPA existente toma el datasource configurado.
