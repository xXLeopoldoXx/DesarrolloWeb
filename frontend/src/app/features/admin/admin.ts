import { Component } from '@angular/core';

import { PaginaPendiente } from '../../shared/pagina-pendiente/pagina-pendiente';

// TODO(Eduardo): reemplazar esta página por la real de "Moderación".
@Component({
  selector: 'app-admin',
  imports: [PaginaPendiente],
  template: `
    <app-pagina-pendiente
      titulo="Moderación"
      descripcion="Revisión de publicaciones reportadas (solo administradoras)."
      responsable="Eduardo"
      icono="shield" />
  `
})
export class Moderacion {}
