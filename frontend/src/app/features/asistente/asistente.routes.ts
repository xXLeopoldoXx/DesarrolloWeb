import { Routes } from '@angular/router';

// Rutas propias del módulo. Agrega aquí las pantallas nuevas (hijas de '').
export const ASISTENTE_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./asistente').then(m => m.Asistente) }
];
