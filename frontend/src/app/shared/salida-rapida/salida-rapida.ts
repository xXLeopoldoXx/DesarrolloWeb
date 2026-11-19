import { Component, inject } from '@angular/core';

import { SalidaDiscretaService } from '../../core/salida-discreta/salida-discreta.service';
import { Icono } from '../icono/icono';

/** Botón de salida rápida. Debe estar visible en TODAS las pantallas, incluido el login. */
@Component({
  selector: 'app-salida-rapida',
  imports: [Icono],
  template: `
    <button
      type="button"
      class="salida"
      (click)="salida.salir()"
      title="Cierra tu sesión y abre otra página al instante (también: Esc dos veces)"
    >
      <app-icono nombre="ocultar" [tamano]="18" />
      <span>Salida rápida</span>
    </button>
  `,
  styles: `
    .salida {
      display: inline-flex;
      align-items: center;
      gap: 0.4rem;
      min-block-size: var(--alto-control);
      padding: 0.5rem 0.9rem;
      border: 0;
      border-radius: 999px;
      background: var(--coral-600);
      color: #fff;
      font-weight: 700;
      font-size: 0.9rem;
      white-space: nowrap;
      cursor: pointer;
      box-shadow: 0 4px 14px rgba(217, 89, 76, 0.35);
    }

    .salida:hover {
      background: var(--coral-700);
    }
  `
})
export class SalidaRapida {
  protected readonly salida = inject(SalidaDiscretaService);
}
