import { Component, signal } from '@angular/core';

/**
 * «Un momento para ti»: círculo de respiración guiada (inhala 4 s · exhala 6 s).
 * Es solo CSS; el botón permite pausar el movimiento (accesibilidad) y con «reducir movimiento»
 * activado en el sistema el círculo queda quieto y queda el texto con las indicaciones.
 */
@Component({
  selector: 'app-respira',
  templateUrl: './respira.html',
  styleUrl: './respira.scss'
})
export class Respira {
  protected readonly pausada = signal(false);

  protected alternar(): void {
    this.pausada.update(valor => !valor);
  }
}
