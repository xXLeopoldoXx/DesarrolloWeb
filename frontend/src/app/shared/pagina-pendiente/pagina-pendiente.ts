import { Component, input } from '@angular/core';

import { Icono, IconoNombre } from '../icono/icono';

/**
 * Marcador de módulo por construir. Cada responsable REEMPLAZA esta página
 * por la real dentro de su carpeta features/<modulo>/.
 */
@Component({
  selector: 'app-pagina-pendiente',
  imports: [Icono],
  template: `
    <section class="pendiente tarjeta">
      <span class="pendiente__icono"><app-icono [nombre]="icono()" [tamano]="36" /></span>
      <h1>{{ titulo() }}</h1>
      <p>{{ descripcion() }}</p>
      <p class="pendiente__resp">Módulo por construir · Responsable: <strong>{{ responsable() }}</strong></p>
    </section>
  `,
  styles: `
    .pendiente {
      max-width: 640px;
      margin: 2rem auto;
      text-align: center;
      padding: 2.5rem 1.5rem;
    }

    .pendiente__icono {
      display: inline-flex;
      padding: 1rem;
      border-radius: 50%;
      background: var(--teal-50);
      color: var(--teal-600);
      margin-bottom: 0.75rem;
    }

    .pendiente__resp {
      color: var(--texto-suave);
      font-size: 0.9rem;
    }
  `
})
export class PaginaPendiente {
  readonly titulo = input.required<string>();
  readonly descripcion = input('');
  readonly responsable = input.required<string>();
  readonly icono = input<IconoNombre>('shield');
}
