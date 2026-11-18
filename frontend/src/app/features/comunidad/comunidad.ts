import { Component } from '@angular/core';

import { PaginaPendiente } from '../../shared/pagina-pendiente/pagina-pendiente';

// TODO(Eduardo): reemplazar esta página por la real de "Comunidad".
@Component({
  selector: 'app-comunidad',
  imports: [PaginaPendiente],
  template: `
    <app-pagina-pendiente
      titulo="Comunidad"
      descripcion="Publicaciones, comentarios y apoyo entre usuarias, con reportes y moderación."
      responsable="Eduardo"
      icono="users" />
  `
})
export class Comunidad {}
