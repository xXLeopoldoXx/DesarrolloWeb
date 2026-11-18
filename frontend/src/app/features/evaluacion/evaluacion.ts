import { Component } from '@angular/core';

import { PaginaPendiente } from '../../shared/pagina-pendiente/pagina-pendiente';

// TODO(Mari): reemplazar esta página por la real de "Mi evaluación".
@Component({
  selector: 'app-evaluacion',
  imports: [PaginaPendiente],
  template: `
    <app-pagina-pendiente
      titulo="Mi evaluación"
      descripcion="Cuestionario con respuestas Sí, No y No estoy segura, y resultados con señales de alerta."
      responsable="Mari"
      icono="clipboard" />
  `
})
export class Evaluacion {}
