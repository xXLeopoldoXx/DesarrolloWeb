import { Component, input } from '@angular/core';

export type IlustracionNombre = 'apoyo' | 'flor' | 'hojas';

/**
 * Ilustraciones propias (SVG en línea) de la app: sin imágenes externas, sin peticiones de red.
 * Son decorativas: van ocultas para lectores de pantalla (aria-hidden) salvo que se pase `etiqueta`.
 * Uso: <app-ilustracion nombre="apoyo" />  ·  se adaptan al ancho del contenedor.
 */
@Component({
  selector: 'app-ilustracion',
  templateUrl: './ilustracion.html',
  styleUrl: './ilustracion.scss'
})
export class Ilustracion {
  readonly nombre = input.required<IlustracionNombre>();
  /** Si se indica, la ilustración se anuncia como imagen con ese texto alternativo. */
  readonly etiqueta = input('');
}
