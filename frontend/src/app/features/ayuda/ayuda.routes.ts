import { Routes } from '@angular/router';

export const AYUDA_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./ayuda').then(m => m.Ayuda) }
];
