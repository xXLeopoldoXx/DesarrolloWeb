# Laboratorio 13 — Formularios en Angular

Proyecto `laboratorio-formularios` (Angular 22, standalone, CSS, routing con carga diferida).

```bash
npm install
npm start      # http://localhost:4200/
npm run build
```

## Vistas

| Ruta | Componente | Enfoque |
|------|------------|---------|
| `/plantilla` | `FormularioPlantillaComponent` | Basado en plantillas (`FormsModule`, `ngModel`) |
| `/reactivo` | `FormularioReactivoComponent` | Reactivo (`ReactiveFormsModule`, `FormBuilder`, `FormArray`) |

`src/app/validators/registro.validators.ts` contiene `edadMinimaValidator` (control) y `passwordsIgualesValidator` (grupo).

## Actividades complementarias

- **Actividad 1 (DNI)** — implementada en el formulario reactivo: campo `dni` obligatorio de exactamente 8 dígitos, con mensajes distintos para requerido y formato inválido.
- Actividades 2, 3 y 4 no se implementaron (la 2 restringiría el correo a un único dominio y la 4 requiere endpoint POST en el backend).

> La validación en Angular mejora la experiencia de usuario, pero el backend Spring Boot debe volver a validar todos los datos.
