import { Routes } from '@angular/router';

// Rutas propias del módulo. Agrega aquí las pantallas nuevas (hijas de '').
export const BIENESTAR_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./bienestar').then(m => m.Bienestar) }
];
