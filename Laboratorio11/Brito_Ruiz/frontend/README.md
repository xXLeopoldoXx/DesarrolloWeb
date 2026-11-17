# Laboratorio 11 — Fundamentos de Angular, componentes y SCSS

Proyecto `catalogo-angular` (Angular 22, componentes standalone, SCSS, sin routing).

```bash
npm install
npm start      # http://localhost:4200/
npm run build  # ng build
```

- `src/app/app.*` — componente raíz con `@for`, `@if`, signals, event binding y `ngModel`.
- `src/app/shared/app-header`, `src/app/shared/app-footer`, `src/app/features/summary-panel` — componentes generados con el CLI.
- `src/styles/_tokens.scss` — partial con variables y mixin, consumido con `@use`.
