import { Routes } from '@angular/router';

export const BIENESTAR_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./bienestar').then(m => m.Bienestar),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'diario' },
      {
        path: 'diario',
        loadComponent: () => import('./diario/diario').then(m => m.Diario)
      },
      {
        path: 'recursos',
        loadComponent: () => import('./recursos/recursos').then(m => m.Recursos)
      }
    ]
  }
];
