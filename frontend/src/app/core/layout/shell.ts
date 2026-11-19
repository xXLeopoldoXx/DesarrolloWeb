import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { AuthService } from '../auth/auth.service';
import { Icono } from '../../shared/icono/icono';
import { SalidaRapida } from '../../shared/salida-rapida/salida-rapida';
import { NAV_ITEMS } from './nav-items';

/** Estructura de toda la zona privada: cabecera, menú lateral/inferior y asistente flotante. */
@Component({
  selector: 'app-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, Icono, SalidaRapida],
  templateUrl: './shell.html',
  styleUrl: './shell.scss'
})
export class Shell {
  protected readonly auth = inject(AuthService);

  protected readonly items = computed(() =>
    NAV_ITEMS.filter(item => !item.soloAdmin || this.auth.esAdmin())
  );
}
