import { Routes } from '@angular/router';

import { Inicio } from './pages/inicio/inicio';
import { Productos } from './pages/productos/productos';
import { ProductoDetalle } from './pages/producto-detalle/producto-detalle';
import { NoEncontrado } from './pages/no-encontrado/no-encontrado';
import { FormularioPlantilla } from './pages/formulario-plantilla/formulario-plantilla';
import { FormularioReactivo } from './pages/formulario-reactivo/formulario-reactivo';

export const routes: Routes = [
  { path: '', component: Inicio },
  { path: 'productos', component: Productos },
  { path: 'productos/:id', component: ProductoDetalle },

  // Formularios - Laboratorio 13
  { path: 'plantilla', component: FormularioPlantilla },
  { path: 'reactivo', component: FormularioReactivo },

  { path: '**', component: NoEncontrado }
];
