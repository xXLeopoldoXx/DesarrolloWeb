import { Routes } from '@angular/router';

export const SEGURIDAD_ROUTES: Routes = [
  { path: '', pathMatch: 'full', loadComponent: () => import('./seguridad').then(m => m.Seguridad) },
  { path: 'plan', loadComponent: () => import('./plan/plan').then(m => m.Plan) },
  { path: 'contactos', loadComponent: () => import('./contactos/contactos').then(m => m.Contactos) },
  { path: 'privacidad', loadComponent: () => import('./privacidad/privacidad').then(m => m.Privacidad) },
];
