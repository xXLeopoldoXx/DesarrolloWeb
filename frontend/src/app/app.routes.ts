import { Routes } from '@angular/router';

import { adminGuard, authGuard, invitadaGuard } from './core/auth/auth.guards';
import { Shell } from './core/layout/shell';

/**
 * Mapa de rutas de la app.
 *
 * REGLA DEL EQUIPO: este archivo NO se edita para agregar pantallas de un módulo.
 * Cada módulo declara sus propias rutas en features/<modulo>/<modulo>.routes.ts
 * y aquí solo se carga con loadChildren (ya está hecho para los 6 módulos).
 */
export const routes: Routes = [
  // Públicas (solo sin sesión)
  {
    path: 'login',
    canActivate: [invitadaGuard],
    loadComponent: () => import('./features/auth/login/login').then(m => m.Login)
  },
  {
    path: 'registro',
    canActivate: [invitadaGuard],
    loadComponent: () => import('./features/auth/registro/registro').then(m => m.Registro)
  },

  // Privadas: dentro del layout con menú
  {
    path: '',
    component: Shell,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'inicio' },
      {
        path: 'inicio',
        loadChildren: () => import('./features/inicio/inicio.routes').then(m => m.INICIO_ROUTES)
      },
      {
        path: 'evaluacion',
        loadChildren: () =>
          import('./features/evaluacion/evaluacion.routes').then(m => m.EVALUACION_ROUTES)
      },
      {
        path: 'seguridad',
        loadChildren: () =>
          import('./features/seguridad/seguridad.routes').then(m => m.SEGURIDAD_ROUTES)
      },
      {
        path: 'ayuda',
        loadChildren: () => import('./features/ayuda/ayuda.routes').then(m => m.AYUDA_ROUTES)
      },
      {
        path: 'bienestar',
        loadChildren: () =>
          import('./features/bienestar/bienestar.routes').then(m => m.BIENESTAR_ROUTES)
      },
      {
        path: 'comunidad',
        loadChildren: () =>
          import('./features/comunidad/comunidad.routes').then(m => m.COMUNIDAD_ROUTES)
      },
      {
        path: 'asistente',
        loadChildren: () =>
          import('./features/asistente/asistente.routes').then(m => m.ASISTENTE_ROUTES)
      },
      {
        path: 'admin',
        canActivate: [adminGuard],
        loadChildren: () => import('./features/admin/admin.routes').then(m => m.ADMIN_ROUTES)
      }
    ]
  },

  { path: '**', redirectTo: '' }
];
