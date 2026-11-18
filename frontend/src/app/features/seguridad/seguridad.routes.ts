import { Routes } from '@angular/router';

// Rutas propias del módulo. Agrega aquí las pantallas nuevas (hijas de '').
export const SEGURIDAD_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./seguridad').then(m => m.Seguridad) }
];
