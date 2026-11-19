# Frontend — Mi espacio seguro

Angular 22 (componentes standalone, signals, SCSS, rutas con carga diferida).

```bash
npm install
npm start      # http://localhost:4200 — reenvía /api al backend (ver proxy.conf.js)
npm test
npm run build
```

Requiere **Node 22.22.3+ o 24**. Si el backend corre en otro puerto: `API_PORT=8081 npm start`.

La guía completa de trabajo en equipo (reglas, reparto, contratos de API) está en [`../docs/CONTRATO.md`](../docs/CONTRATO.md).

## Estructura

```
src/app/
  core/        auth (servicio, guards, interceptor), layout (shell + menú), salida rápida, config
  shared/      componentes compartidos: app-icono, app-salida-rapida, app-pagina-pendiente
  features/    un módulo por carpeta: inicio, ayuda, evaluacion, seguridad, bienestar,
               comunidad, asistente, admin  (+ auth: login y registro)
```

Cada módulo define sus rutas en `features/<modulo>/<modulo>.routes.ts`; `app.routes.ts` solo las carga.
