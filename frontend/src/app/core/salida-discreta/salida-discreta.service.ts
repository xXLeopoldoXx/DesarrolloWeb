import { inject, Injectable } from '@angular/core';

import { AuthService } from '../auth/auth.service';
import { URL_SALIDA_RAPIDA } from '../config';

/**
 * "Salida rápida": cierra la sesión y reemplaza la página por un sitio neutro.
 * Se activa con el botón visible en toda la app o presionando Esc dos veces seguidas.
 *
 * Limitación honesta: no puede borrar el historial del navegador. Por eso la app
 * recomienda usar una ventana privada si el dispositivo se comparte.
 */
@Injectable({ providedIn: 'root' })
export class SalidaDiscretaService {
  private readonly auth = inject(AuthService);

  private ultimoEscape = 0;

  salir(): void {
    this.auth.limpiarSesion();
    window.location.replace(URL_SALIDA_RAPIDA);
  }

  /** Esc dos veces en menos de 800 ms equivale a pulsar el botón. */
  registrarEscape(ahora: number = Date.now()): void {
    if (ahora - this.ultimoEscape < 800) {
      this.salir();
      return;
    }
    this.ultimoEscape = ahora;
  }
}
