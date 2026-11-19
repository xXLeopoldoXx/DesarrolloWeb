# Laboratorio 14 — Servicios y consumo de APIs en Angular

Proyecto `laboratorio-servicios-api` (Angular 22, standalone, CSS, `HttpClient`). Consume la API pública
[JSONPlaceholder](https://jsonplaceholder.typicode.com/users); las operaciones de escritura son simuladas y no persisten.

```bash
npm install
npm start      # http://localhost:4200/usuarios
npm run build
npm test
```

## Estructura

- `src/app/usuarios/usuario.model.ts` — interfaces `Usuario` y `UsuarioEntrada`.
- `src/app/usuarios/usuario.service.ts` — `UsuarioService`: GET, POST, PUT, PATCH y DELETE con `HttpClient`.
- `src/app/usuarios/usuarios.*` — componente con formulario reactivo, tabla y estados de carga / éxito / error.
- `src/app/app.config.ts` — `provideHttpClient()` junto a `provideRouter(routes)`.

## Actividades complementarias implementadas

- Cuadro de búsqueda local por nombre o correo.
- Contador de solicitudes exitosas.
- Botón "Cambiar correo" que ejecuta PATCH sobre el correo.
- Prueba unitaria del servicio con `provideHttpClientTesting()` (`usuario.service.spec.ts`).

## Conectar con Spring Boot

Cambiar `apiUrl` por `http://localhost:8080/api/usuarios` (o el puerto que use el backend) y habilitar CORS para
`http://localhost:4200` en el controlador.
