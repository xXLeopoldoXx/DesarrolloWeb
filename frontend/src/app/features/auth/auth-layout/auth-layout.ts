import { Component, input } from '@angular/core';

import { Icono } from '../../../shared/icono/icono';
import { SalidaRapida } from '../../../shared/salida-rapida/salida-rapida';

/** Marco compartido de login y registro: panel de bienvenida + formulario proyectado. */
@Component({
  selector: 'app-auth-layout',
  imports: [Icono, SalidaRapida],
  templateUrl: './auth-layout.html',
  styleUrl: './auth-layout.scss'
})
export class AuthLayout {
  readonly titulo = input.required<string>();
  readonly subtitulo = input('');
}
