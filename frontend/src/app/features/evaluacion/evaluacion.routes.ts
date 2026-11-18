import { Routes } from '@angular/router';

// Rutas propias del módulo. Agrega aquí las pantallas nuevas (hijas de '').
export const EVALUACION_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./evaluacion').then(m => m.Evaluacion) }
];
