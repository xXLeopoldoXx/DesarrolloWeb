import { Routes } from '@angular/router';

export const EVALUACION_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./cuestionario/cuestionario')
        .then(m => m.Cuestionario)
  },
  {
    path: 'historial',
    loadComponent: () =>
      import('./historial/historial')
        .then(m => m.Historial)
  },
  {
    path: 'resultados/:id',
    loadComponent: () =>
      import('./resultados/resultados')
        .then(m => m.Resultados)
  }
];
