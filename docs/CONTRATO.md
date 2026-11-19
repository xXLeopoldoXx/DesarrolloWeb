# Contrato del equipo — Mi espacio seguro

```
Angular (frontend/)  ──/api──▶  Spring Boot (src/)  ──JPA──▶  PostgreSQL (riesgo_db)
```

## 1. Correrlo

| | Comando | Requisitos |
|---|---|---|
| Backend (raíz) | `./mvnw spring-boot:run` · `./mvnw test` | Java 21, Postgres con `riesgo_db`, variables abajo |
| Frontend | `cd frontend && npm install && npm start` · `npm test` | Node **22.22.3+ o 24** |

Variables del backend: `JWT_SECRET_BASE64` (Base64 de 32+ bytes), `DB_PASSWORD` (tu Postgres), `SERVER_PORT` (opcional, `8081` si el 8080 está ocupado), `GEMINI_API_KEY` (solo para el asistente). Si usas 8081: `API_PORT=8081 npm start`.
Cuentas de prueba: `usuario / Usuario123*` · `admin / Admin123*` (o crear una en `/registro`).

## 2. Reglas

1. Toda tabla de módulo lleva `usuario_id`. El id **nunca** viene del cliente: `Usuario u = usuarioService.actual(authentication);`
2. Recurso ajeno ⇒ **404**: `repo.findByIdAndUsuarioId(id, u.getId()).orElseThrow(() -> new RecursoNoEncontradoException("..."))`.
3. Nunca exponer `username` ni datos de otras usuarias. En comunidad solo `seudonimo`.
4. `@Valid` + DTO `record` con validaciones. Errores por `GlobalExceptionHandler`.
5. Si guardas datos de la usuaria, implementa `LimpiezaDatosUsuaria` (`@Transactional`).
6. Sin datos personales en logs ni `console.log`.
7. **No editar** `SecurityConfig`, `app.routes.ts`, `nav-items.ts`, `styles.scss` sin avisar. Las rutas `/api/mi/**` y `/api/comunidad/**` ya están protegidas.

## 3. Ya está hecho

- **Backend:** `POST /api/auth/registro` y `/login`, `GET /api/mi/perfil`, CORS, `UsuarioService.actual()`, `RecursoNoEncontradoException` (404), `LimpiezaDatosUsuaria`.
- **Frontend:** login/registro, guards, interceptor del token, layout con menú, salida rápida, Inicio, Ayuda, `app-icono`, `app-pagina-pendiente`, `mensajeDeError()`, `API_URL`.

## 4. Convenciones

- **Backend** (`src/main/java/com/utp/proyectoriesgo/`): capas `model/ repository/ dto/ service/ controller/`. FK: `@Column(name="usuario_id", nullable=false) private Long usuarioId;`. Tablas `snake_case` plural. Columnas nuevas en tablas existentes: `nullable`.
- **Frontend** (`frontend/src/app/features/<modulo>/`): reemplazar `<modulo>.ts` (página pendiente), agregar rutas en `<modulo>.routes.ts`, servicio propio con `HttpClient` + `API_URL`. Formularios reactivos. Estilos con `var(--teal-600)` y clases `.tarjeta .boton .campo .alerta`. Sin backend aún ⇒ datos de mentira.

```ts
@Injectable({ providedIn: 'root' })
export class XService {
  private readonly http = inject(HttpClient);
  listar() { return this.http.get<X[]>(`${API_URL}/mi/x`); }
}
```

## 5. Qué crea cada uno

### Mari — Mi evaluación

**Backend**
```
model/        EvaluacionUsuaria   (id, usuarioId, fecha, puntaje, nivel, alertaCritica)  → evaluaciones_usuaria
              RespuestaEvaluacion (id, evaluacionId, factor, respuesta)                  → respuestas_evaluacion
              Factor (enum, 9)    AMENAZA_MUERTE, ACCESO_ARMAS, ESTRANGULAMIENTO_PREVIO, DENUNCIAS_PREVIAS,
                                  INCUMPLIMIENTO_MEDIDA_PROTECCION, ACOSO_O_PERSECUCION, SEPARACION_RECIENTE,
                                  CONTROL_EXTREMO, CONSUMO_ALCOHOL_DROGAS_AGRESOR   (con texto y peso de RiesgoCalculador)
              Respuesta (enum)    SI, NO, NO_SEGURA
repository/   EvaluacionUsuariaRepository   findByUsuarioIdOrderByFechaDesc · findByIdAndUsuarioId · deleteByUsuarioId
              RespuestaEvaluacionRepository findByEvaluacionId · deleteByEvaluacionIdIn
dto/          AutoevaluacionRequest(List<RespuestaItem> respuestas)   RespuestaItem(Factor factor, Respuesta respuesta)
              ResultadoResponse(id, fecha, nivel, puntaje, alertaCritica, List<String> senales, List<String> porConfirmar)
              ResumenEvaluacionResponse(id, fecha, nivel)
service/      CalculadoraAutoevaluacion   ResultadoResponse calcular(List<RespuestaItem>)
              AutoevaluacionService       crear(Long usuarioId, AutoevaluacionRequest) · listar(Long) · detalle(Long, Long) · eliminar(Long, Long)
                                          + implements LimpiezaDatosUsuaria
controller/   MiEvaluacionController   POST /api/mi/evaluaciones (201) · GET /api/mi/evaluaciones · GET …/{id} · DELETE …/{id} (204)
test/         CalculadoraAutoevaluacionTest · AutoevaluacionServiceTest · MiEvaluacionControllerTest (otra usuaria ⇒ 404)
```
Reglas: `SI` suma el peso; `NO_SEGURA` no suma y va a `porConfirmar`; alerta crítica igual que `RiesgoCalculador`. **No afirmar probabilidad de feminicidio.**

**Frontend** `features/evaluacion/`
```
evaluacion.models.ts     tipos de las respuestas del API
evaluacion.service.ts    crear(respuestas) · listar() · obtener(id) · eliminar(id)
preguntas.ts             PREGUNTAS: { factor, texto, ayuda }[]
cuestionario/            formulario reactivo, 3 opciones por pregunta (Sí / No / No estoy segura) · enviar() → navega a resultados/:id
resultados/              nivel (con color), señales de alerta, «por confirmar», enlaces a Ayuda y Mi seguridad
historial/               evaluaciones anteriores + eliminar
evaluacion.routes.ts     '' → cuestionario · 'historial' · 'resultados/:id'
```

### Anheli — Mi seguridad + diseño

**Backend**
```
model/        PlanAccion (id, usuarioId, texto, completada)            → plan_acciones
              LugarSeguro (id, usuarioId, nombre, referencia, nota)    → lugares_seguros
              ContactoConfianza (id, usuarioId, nombre, telefono, relacion) → contactos_confianza
repository/   PlanAccionRepository · LugarSeguroRepository · ContactoConfianzaRepository
              (findByUsuarioId… · findByIdAndUsuarioId · deleteByUsuarioId)
dto/          AccionRequest(texto) · AccionEstadoRequest(completada) · LugarRequest · ContactoRequest + sus Response
service/      PlanSeguridadService  listarAcciones · crearAccion · marcarAccion(id, completada) · eliminarAccion
                                    listarLugares · crearLugar · eliminarLugar     + implements LimpiezaDatosUsuaria
              ContactoService       listar · crear · actualizar · eliminar         + implements LimpiezaDatosUsuaria
              CuentaService         eliminarCuenta(Usuario): llama a List<LimpiezaDatosUsuaria> y luego borra la usuaria
controller/   MiPlanController      /api/mi/plan/acciones (GET, POST, PATCH /{id}, DELETE /{id}) · /api/mi/plan/lugares (GET, POST, DELETE /{id})
              MiContactosController /api/mi/contactos (GET, POST, PUT /{id}, DELETE /{id})
              MiCuentaController    DELETE /api/mi/cuenta (204)
test/         un test de servicio y uno de controlador por clase (aislamiento entre usuarias)
```

**Frontend** `features/seguridad/`
```
seguridad.service.ts     acciones, lugares, contactos y eliminarCuenta()
plan/                    checklist de acciones (marcar/eliminar) + lugares seguros
contactos/               red de confianza: lista + formulario
privacidad/              botón «Eliminar mis datos» con confirmación → eliminarCuenta() → auth.cerrarSesion()
seguridad.routes.ts      '' (3 tarjetas) · 'plan' · 'contactos' · 'privacidad'
```
**Diseño:** dueña de `styles.scss` (tokens) y del pulido visual de toda la app.

### Natty — Bienestar

**Backend**
```
model/        EntradaDiario (id, usuarioId, fecha, estadoAnimo, texto)  → diario_entradas
repository/   EntradaDiarioRepository   findByUsuarioIdOrderByFechaDesc · findByIdAndUsuarioId · deleteByUsuarioId
dto/          DiarioRequest(@Min(1) @Max(5) int estadoAnimo, @NotBlank @Size(max=2000) String texto) · DiarioResponse
service/      DiarioService   listar(Long) · crear(Long, DiarioRequest) · actualizar(Long, Long, DiarioRequest) · eliminar(Long, Long)
                              + implements LimpiezaDatosUsuaria
controller/   MiDiarioController   GET/POST /api/mi/diario · PUT/DELETE /api/mi/diario/{id}
test/         DiarioServiceTest · MiDiarioControllerTest (otra usuaria ⇒ 404)
```

**Frontend** `features/bienestar/`
```
bienestar.service.ts     listar() · crear() · actualizar() · eliminar()
diario/                  lista de entradas + formulario (ánimo 1–5, texto) · editar · eliminar con confirmación
recursos/                autocuidado y «señales de violencia» (contenido estático)
bienestar.routes.ts      '' → diario · 'recursos'
```

### Eduardo — Comunidad y Moderación

**Backend**
```
model/        Publicacion (id, usuarioId, texto, fecha, oculta)                 → publicaciones
              Comentario  (id, publicacionId, usuarioId, texto, fecha)          → comentarios
              Reporte     (id, publicacionId, usuarioId, motivo, fecha)         → reportes
repository/   PublicacionRepository  findByOcultaFalseOrderByFechaDesc(Pageable)
              ComentarioRepository   findByPublicacionIdOrderByFechaAsc · countByPublicacionId
              ReporteRepository      existsByPublicacionIdAndUsuarioId · findAllByOrderByFechaDesc
dto/          PublicacionRequest(@NotBlank @Size(max=500) texto)   PublicacionResponse(id, autor, texto, fecha, comentarios, esMia)
              ComentarioRequest(max 300) · ComentarioResponse   ReporteRequest(motivo) · ReporteResponse
service/      ComunidadService   listar(Usuario, Pageable) · publicar(Usuario, req) · eliminar(Usuario, id)  ← dueña o ADMIN
                                 comentar(Usuario, id, req) · comentarios(id) · reportar(Usuario, id, req)  ← una vez por usuaria
                                 ocultar(id) · reportes()           + implements LimpiezaDatosUsuaria
controller/   ComunidadController    /api/comunidad/publicaciones (GET, POST) · /{id} (DELETE) · /{id}/comentarios (GET, POST) · /{id}/reportes (POST)
              ModeracionController   GET /api/admin/comunidad/reportes · PATCH /api/admin/comunidad/publicaciones/{id}/ocultar  (solo ADMIN)
test/         ComunidadServiceTest · ComunidadControllerTest · ModeracionControllerTest (USER ⇒ 403)
```
`autor` = `seudonimo` (si es null, «Anónima»). Nunca `username`.

**Frontend** `features/comunidad/` y `features/admin/`
```
comunidad.service.ts     listar() · publicar() · eliminar() · comentarios() · comentar() · reportar()
muro/                    feed + formulario de publicar + aviso «No publiques datos que te identifiquen»
publicacion-card/        autor, texto, comentarios desplegables, Reportar, Eliminar (si esMia)
comunidad.routes.ts      '' → muro
admin/moderacion.ts      lista de reportes + botón «Ocultar» (solo ADMIN; ya hay guard)
```

### Leo — Base, Inicio, Ayuda e integración

Hecho: base, Inicio, Ayuda y **Asistente** (sección 6). Pendiente: revisar y fusionar los PR, y que **Inicio** muestre el «resumen de actividades» (última evaluación y acciones pendientes del plan) cuando existan esos módulos.

## 6. Asistente con IA (Leo) — hecho

Gemini (plan gratuito de Google AI Studio). Rama `feature/asistente`.

**Para usarlo:** crear la clave en Google AI Studio y ponerla en **uno** de estos dos sitios (`GEMINI_MODEL` es opcional; por defecto `gemini-3.5-flash-lite`):
- variable de entorno `GEMINI_API_KEY`, o
- archivo local `config/application.properties` con `app.gemini.api-key=tu-clave` (la carpeta `config/` está en `.gitignore`; Spring Boot la carga sola al arrancar desde la raíz).

Sin clave todo funciona y el chat avisa «aún no está configurado». **Nunca** poner la clave en `src/main/resources/application.properties` ni en ningún archivo versionado.

```
Backend   controller/AsistenteController   POST /api/asistente/mensajes {mensaje ≤500, historial ≤6} → {respuesta, emergencia} · GET /api/asistente/estado → {disponible}
          service/AsistenteService          responder(usuario, request) · construirTurnos(historial, mensaje)
          service/GeminiClient              generar(instrucciones, turnos) · configurado()          (RestClient → generateContent)
          service/DetectorPeligro           indicaPeligro(mensaje)
          service/LimitadorMensajes         registrar(usuario)  (20 mensajes/hora ⇒ 429)
          exception/                        AsistenteNoDisponibleException (503) · LimiteMensajesException (429)
          application.properties            app.gemini.* · app.asistente.max-mensajes-hora
Frontend  features/asistente/asistente.ts + .html + .scss   chat: burbujas, sugerencias, «escribiendo…», tarjeta de emergencia
          features/asistente/asistente.service.ts            enviar(mensaje, historial) · estado()
```
Reglas (ya implementadas y con pruebas):
- Instrucciones fijas: orienta de forma general, no diagnostica, no da «probabilidad de feminicidio», no pide datos personales.
- **Nunca** recibe evaluaciones, diario ni publicaciones; el nombre de usuaria tampoco se envía. La conversación no se guarda en el servidor ni en el navegador.
- Mensaje de peligro (frases como «me va a matar», «quiero morir»): respuesta fija con 105 y Línea 100, **sin llamar al modelo** y aunque se haya agotado el límite.
- La clave viaja solo en la cabecera `x-goog-api-key`. Los logs no guardan el texto de la usuaria (`spring.mvc.log-resolved-exception=false`).
- El plan gratuito de Google puede usar lo enviado para mejorar sus productos: otra razón para no enviar nada personal.

## 7. Git

`feature/<modulo>` desde `integracion` ⇒ PR hacia `integracion` ⇒ al final `integracion` ⇒ `main`. Antes del PR: `./mvnw test`, `npm test` y `npm run build` en verde.

## 8. Terminado =

- [ ] Endpoints probados con **dos** usuarias: la B no ve lo de la A.
- [ ] Tests de servicio y controlador.
- [ ] Pantalla con estados de carga, error y vacío; se ve bien en móvil.
- [ ] Implementa `LimpiezaDatosUsuaria` si guarda datos.
