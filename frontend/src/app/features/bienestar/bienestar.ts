import { Component } from '@angular/core';

import { PaginaPendiente } from '../../shared/pagina-pendiente/pagina-pendiente';

// TODO(Natty): reemplazar esta página por la real de "Bienestar".
@Component({
  selector: 'app-bienestar',
  imports: [PaginaPendiente],
  template: `
    <app-pagina-pendiente
      titulo="Bienestar"
      descripcion="Diario personal privado y recursos de autocuidado."
      responsable="Natty"
      icono="heart" />
  `
})
export class Bienestar {}
