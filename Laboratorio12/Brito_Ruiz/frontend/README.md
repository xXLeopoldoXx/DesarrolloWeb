# Laboratorio 12 — Rutas y comunicación entre componentes

Proyecto `frontend-semana12` (Angular 22, standalone, SCSS, con routing).

```bash
npm install
npm start      # http://localhost:4200/
npm run build
```

## Mapa de rutas

| Ruta | Componente |
|------|------------|
| `/` | `Inicio` |
| `/productos` | `Productos` (padre de `ProductoCard`) |
| `/productos/:id` | `ProductoDetalle` |
| `/categorias` | `Categorias` (Reto A) |
| `**` | `NoEncontrado` (siempre al final) |

## Retos de consolidación

- **A** — `pages/categorias` + ruta `/categorias` + enlace en el menú; muestra las categorías únicas de `PRODUCTOS`.
- **B** — `ProductoCard` tiene `destacado = input(false)`; `Productos` marca como destacado el producto con precio mayor a S/ 1,000.
- **C** — `ProductoCard` emite `agregarComparacion = output<number>()`; `Productos` guarda los ids y muestra cuántos se añadieron.
- **D** — `/productos/1` y `/productos/999` coinciden con la **misma** ruta `productos/:id`, por lo que se carga el mismo componente `ProductoDetalle`. La diferencia está en el dato: el componente lee el `:id` con `ActivatedRoute`, lo convierte a número y busca en `PRODUCTOS`. Con `1` encuentra un producto y muestra su detalle; con `999` `find()` devuelve `undefined` y la plantilla entra por el `@else` ("Producto no encontrado"). La ruta `**` solo se activa cuando ninguna ruta coincide con la URL.
