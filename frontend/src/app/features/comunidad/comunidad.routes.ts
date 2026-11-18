import { Routes } from '@angular/router';

// Rutas propias del módulo. Agrega aquí las pantallas nuevas (hijas de '').
export const COMUNIDAD_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./comunidad').then(m => m.Comunidad) }
];
