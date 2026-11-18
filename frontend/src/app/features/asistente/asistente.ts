import { Component } from '@angular/core';

import { PaginaPendiente } from '../../shared/pagina-pendiente/pagina-pendiente';

// TODO(Leo): reemplazar esta página por la real de "Asistente de orientación".
@Component({
  selector: 'app-asistente',
  imports: [PaginaPendiente],
  template: `
    <app-pagina-pendiente
      titulo="Asistente de orientación"
      descripcion="Chat con IA (Gemini) que responde dudas generales. Nunca recibe tus datos personales."
      responsable="Leo"
      icono="chat" />
  `
})
export class Asistente {}
