# Contrato del equipo — Mi espacio seguro

> Léanlo completo antes de empezar. Si algo de aquí no les sirve, **se cambia aquí primero** y se avisa al grupo.

```
Angular (frontend/)  ──HTTP /api──▶  Spring Boot (src/)  ──JPA──▶  PostgreSQL (riesgo_db)
  pantallas + HttpClient              controllers → services → repositories
  ng serve  :4200   (proxy)           :8080 (u 8081)
```

## 1. Cómo correrlo

**Backend** (raíz del repo). Necesita Java 21, PostgreSQL con la base `riesgo_db` y estas variables de entorno:

| Variable | Para qué | Valor |
|---|---|---|
| `JWT_SECRET_BASE64` | firma de los tokens | cualquier Base64 de 32+ bytes. Para desarrollo: `MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=` |
| `DB_PASSWORD` | clave de **tu** PostgreSQL local | la tuya (ya no está escrita en el repo) |
| `SERVER_PORT` | opcional | `8081` si el 8080 está ocupado (a Leo le pasa: tiene Jenkins) |

```bash
./mvnw spring-boot:run        # arranca la API
./mvnw test                   # tests (55 al armar la base)
```

**Frontend** (`frontend/`). Necesita Node **22.22.3+ o 24** (Angular 22 no corre en versiones anteriores).

```bash
cd frontend
npm install
npm start                     # http://localhost:4200  (reenvía /api al backend)
npm test                      # pruebas unitarias
npm run build
```

Si el backend corre en 8081: `API_PORT=8081 npm start` (PowerShell: `$env:API_PORT=8081; npm start`).

**Cuentas de prueba** (las crea `DatosSeguridadIniciales`): `usuario / Usuario123*` y `admin / Admin123*`. Cualquiera puede crear otra desde `/registro`.

## 2. Reglas de oro (por ser una app de víctimas de violencia)

1. **Cada dato pertenece a una usuaria.** Toda tabla de módulo lleva `usuario_id`. El id **nunca** llega desde el cliente: se obtiene con `usuarioService.actual(authentication).getId()`.
2. **Recurso ajeno = 404, no 403.** Si piden `/api/mi/.../{id}` de otra usuaria, lanzar `RecursoNoEncontradoException` (así no se revela que existe). Busquen siempre por `id` **y** `usuarioId`.
3. **Nunca exponer** `username`, contraseñas, ni datos de otras usuarias. En la comunidad solo se muestra el `seudonimo`.
4. **`/api/evaluaciones/**` es solo ADMIN** (modelo profesional anterior). Las usuarias usan `/api/mi/**`.
5. **Nada de datos personales en logs, ni al asistente de IA, ni en URLs.**
6. Validar en el backend con `@Valid` + anotaciones en los DTO (`record`). La validación de Angular es solo comodidad.
7. Los errores salen por `GlobalExceptionHandler` (`{fecha, estado, error, mensaje, ruta}`); el frontend los muestra con `mensajeDeError()`.
8. Cada módulo que guarde datos implementa `LimpiezaDatosUsuaria` (ver `service/LimpiezaDatosUsuaria.java`) para que "Eliminar mis datos" funcione.

## 3. Qué ya está hecho (no lo reescriban)

**Backend:** `POST /api/auth/registro`, `POST /api/auth/login`, `GET /api/mi/perfil`, CORS, `SecurityConfig` con las rutas `/api/mi/**`, `/api/comunidad/**` y `/api/asistente/**` ya protegidas, `UsuarioService.actual(...)`, `RecursoNoEncontradoException` (404), `UsuarioExistenteException` (409).
**Frontend:** login y registro, guards, interceptor del token, layout con menú (escritorio y móvil), **salida rápida** (botón + Esc dos veces), Inicio, Ayuda, tokens de diseño en `styles.scss`, componentes `app-icono` y `app-pagina-pendiente`.

> **No editen** `SecurityConfig`, `app.routes.ts`, `nav-items.ts` ni `styles.scss` sin avisar: son compartidos. Los módulos no los necesitan.

## 4. Convenciones para agregar un módulo

**Backend** — misma estructura por capas que ya existe (`controller/`, `service/`, `model/`, `repository/`, `dto/`). Prefijo de rutas: `/api/mi/<recurso>`. Tablas en `snake_case` plural. FK como `@Column(name = "usuario_id", nullable = false) private Long usuarioId;` (sin `@ManyToOne`, para que los módulos no se acoplen). Columnas nuevas en tablas **existentes** deben ser `nullable` (`ddl-auto=update`).

**Frontend** — todo dentro de `frontend/src/app/features/<modulo>/`:
- Reemplazar el componente pendiente (`<modulo>.ts`) por el real y agregar pantallas en `<modulo>.routes.ts`.
- Servicio propio `<modulo>.service.ts` con `HttpClient` y `API_URL` (de `core/config.ts`). **No** tocar el token: lo agrega el interceptor.
- Formularios reactivos con validación (como en el Lab 13). Estilos: usar las variables `var(--teal-600)`, clases `.tarjeta`, `.boton`, `.campo`, `.alerta`.
- Mientras el backend no esté listo, pueden trabajar con datos de mentira (como en el Lab 12) y luego cambiar a `HttpClient`.

## 5. Reparto y contrato por módulo

| Responsable | Módulo |
|---|---|
| **Leo** | Base (hecha), Inicio, Ayuda, integración final, chat del asistente (frontend) |
| **Natty** | Base de seguridad (hecha), Bienestar/Diario, asistente con Gemini (backend) |
| **Mari** | Mi evaluación + Mis resultados |
| **Anheli** | Mi seguridad (plan, red de confianza, privacidad) + diseño visual |
| **Eduardo** | Comunidad + Moderación |

### Mari — Evaluación (`features/evaluacion/`)
Tablas: `evaluaciones_usuaria` (id, usuario_id, fecha, puntaje, nivel, alerta_critica), `respuestas_evaluacion` (id, evaluacion_id, factor, respuesta).
Respuesta: `SI` | `NO` | `NO_SEGURA`. Factores: los de `RiesgoCalculador` (`AMENAZA_MUERTE`, `ACCESO_ARMAS`, `ESTRANGULAMIENTO_PREVIO`, `DENUNCIAS_PREVIAS`, `INCUMPLIMIENTO_MEDIDA_PROTECCION`, `ACOSO_O_PERSECUCION`, `SEPARACION_RECIENTE`, `CONTROL_EXTREMO`, `CONSUMO_ALCOHOL_DROGAS_AGRESOR`).
Reglas: `SI` suma el peso del factor; `NO_SEGURA` **no suma** pero se lista como «por confirmar». La app **no afirma probabilidad de feminicidio**: muestra señales de alerta, nivel (BAJO/MEDIO/ALTO/MUY_ALTO) y recomendaciones. Los pesos actuales suman hasta 140: si se muestra un índice 0–100 hay que normalizarlo y que el equipo lo valide.

| Método y ruta | Cuerpo → Respuesta |
|---|---|
| `POST /api/mi/evaluaciones` | `{respuestas:[{factor, respuesta}]}` → 201 `{id, fecha, nivel, puntaje, alertaCritica, senales:[texto], porConfirmar:[texto]}` |
| `GET /api/mi/evaluaciones` | → lista (más reciente primero) |
| `GET /api/mi/evaluaciones/{id}` | → detalle con respuestas |
| `DELETE /api/mi/evaluaciones/{id}` | → 204 |

### Anheli — Mi seguridad (`features/seguridad/`)
Tablas: `plan_acciones` (usuario_id, texto, completada), `lugares_seguros` (usuario_id, nombre, referencia, nota), `contactos_confianza` (usuario_id, nombre, telefono, relacion).

| Método y ruta | Cuerpo → Respuesta |
|---|---|
| `GET/POST /api/mi/plan/acciones` | `{texto}` → acción |
| `PATCH/DELETE /api/mi/plan/acciones/{id}` | `{completada}` / 204 |
| `GET/POST /api/mi/plan/lugares` y `DELETE …/{id}` | `{nombre, referencia, nota}` |
| `GET/POST /api/mi/contactos` y `PUT/DELETE …/{id}` | `{nombre, telefono, relacion}` |
| `DELETE /api/mi/cuenta` | borra todo vía `List<LimpiezaDatosUsuaria>` y la cuenta → 204 |

Además: ser la dueña de los **tokens de diseño** (`styles.scss`) y del pulido visual de toda la app.

### Natty — Bienestar / Diario (`features/bienestar/`)
Tabla: `diario_entradas` (usuario_id, fecha, estado_animo 1–5, texto máx. 2000).

| Método y ruta | Cuerpo → Respuesta |
|---|---|
| `GET /api/mi/diario` | → lista (más reciente primero) |
| `POST /api/mi/diario` | `{estadoAnimo, texto}` → 201 |
| `PUT/DELETE /api/mi/diario/{id}` | actualizar / 204 |

Opcional: recursos de autocuidado y «aprende sobre señales de violencia» (contenido estático).

### Eduardo — Comunidad y Moderación (`features/comunidad/`, `features/admin/`)
Tablas: `publicaciones` (usuario_id, texto máx. 500, fecha, oculta), `comentarios` (publicacion_id, usuario_id, texto máx. 300, fecha), `reportes` (publicacion_id, usuario_id, motivo, fecha).
Solo se muestra el **seudónimo**. Aviso visible: «No publiques datos que te identifiquen (nombre, dirección, teléfono)». Sin mensajes directos.

| Método y ruta | Cuerpo → Respuesta |
|---|---|
| `GET /api/comunidad/publicaciones` | → `[{id, autor, texto, fecha, comentarios, esMia}]` (las ocultas no salen) |
| `POST /api/comunidad/publicaciones` | `{texto}` → 201 |
| `DELETE /api/comunidad/publicaciones/{id}` | dueña o ADMIN → 204 |
| `GET/POST /api/comunidad/publicaciones/{id}/comentarios` | `{texto}` |
| `POST /api/comunidad/publicaciones/{id}/reportes` | `{motivo}` → 201 |
| `GET /api/admin/comunidad/reportes` | solo ADMIN |
| `PATCH /api/admin/comunidad/publicaciones/{id}/ocultar` | solo ADMIN → 204 |

### Leo + Natty — Asistente (`features/asistente/`)
`POST /api/asistente/mensajes` · cuerpo `{mensaje (≤500), historial?:[{rol:'usuaria'|'asistente', texto}] (máx. 6)}` → `{respuesta}`.
- Backend llama a la API de Gemini (plan gratuito de Google AI Studio). Clave en la variable `GEMINI_API_KEY`; nombre del modelo en la propiedad `app.gemini.model`. **Nunca** subir la clave al repo.
- Texto de instrucciones fijo (prompt de sistema): asistente de orientación, responde solo con información general, no da diagnósticos ni «probabilidad de feminicidio», no pide datos personales.
- **Seguridad:** el asistente **no recibe** evaluaciones, diario ni publicaciones. Si el mensaje indica peligro inmediato, responder con texto fijo (105 y Línea 100) **sin llamar al modelo**. Límite por usuaria (p. ej. 20 mensajes por hora → 429).
- El plan gratuito de Google puede usar lo enviado para mejorar sus productos: otra razón para no enviar nada personal.

## 6. Git

1. Cada quien trabaja en `feature/<modulo>` creada desde `integracion` (la base ya incluida).
2. PR pequeños hacia `integracion`; revisión cruzada antes de aprobar. `main` solo al final.
3. Antes de abrir el PR: `./mvnw test` y `npm test` y `npm run build` en verde.
4. Conflictos probables: solo en archivos compartidos (sección 3). Si necesitan cambiar uno, avisen.

## 7. Definición de «terminado» por módulo

- [ ] Endpoints del contrato funcionando y probados con `curl`/Postman con dos usuarias distintas (la B **no** puede ver lo de la A).
- [ ] Tests del servicio y del controlador (ejemplos en `src/test/`).
- [ ] Pantallas con estados de carga, error y vacío; funciona en móvil.
- [ ] Implementa `LimpiezaDatosUsuaria` si guarda datos.
- [ ] Sin `console.log` ni datos personales en logs.

## 8. Pendiente de decidir como equipo

- ¿Mostramos un índice 0–100 o solo nivel y señales? (hoy: solo nivel y señales).
- Números y servicios de la pantalla de Ayuda: verificarlos en fuentes oficiales antes de la demo.
- Eliminar `DatosSeguridadIniciales` (cuentas `usuario`/`admin` con clave conocida) antes de cualquier despliegue real.
