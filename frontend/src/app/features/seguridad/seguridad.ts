import { Component } from '@angular/core';

import { PaginaPendiente } from '../../shared/pagina-pendiente/pagina-pendiente';

// TODO(Anheli): reemplazar esta página por la real de "Mi seguridad".
@Component({
  selector: 'app-seguridad',
  imports: [PaginaPendiente],
  template: `
    <app-pagina-pendiente
      titulo="Mi seguridad"
      descripcion="Plan de seguridad, red de confianza y privacidad de tus datos."
      responsable="Anheli"
      icono="shield" />
  `
})
export class Seguridad {}
