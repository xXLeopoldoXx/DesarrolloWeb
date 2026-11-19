import { Component, HostListener, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { SalidaDiscretaService } from './core/salida-discreta/salida-discreta.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  template: '<router-outlet />'
})
export class App {
  private readonly salida = inject(SalidaDiscretaService);

  // Esc dos veces seguidas = salida rápida, desde cualquier pantalla.
  @HostListener('document:keydown.escape')
  protected alPresionarEscape(): void {
    this.salida.registrarEscape();
  }
}
