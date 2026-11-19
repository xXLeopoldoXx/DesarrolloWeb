import { Routes } from '@angular/router';

// Rutas propias del módulo. Agrega aquí las pantallas nuevas (hijas de '').
export const ADMIN_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./admin').then(m => m.Moderacion) }
];
