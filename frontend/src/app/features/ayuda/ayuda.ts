import { Component } from '@angular/core';

import { Icono } from '../../shared/icono/icono';

interface Servicio {
  nombre: string;
  descripcion: string;
  /** Número para llamar (se usa en tel:). Vacío si solo hay enlace web. */
  telefono?: string;
  /** Texto del botón */
  accion: string;
  enlace?: string;
  urgente?: boolean;
}

/**
 * Directorio de ayuda. Datos estáticos: si cambian los números oficiales se editan aquí.
 * IMPORTANTE: verificar los datos en los sitios oficiales antes de la presentación.
 */
@Component({
  selector: 'app-ayuda',
  imports: [Icono],
  templateUrl: './ayuda.html',
  styleUrl: './ayuda.scss'
})
export class Ayuda {
  protected readonly servicios: Servicio[] = [
    {
      nombre: 'Policía Nacional del Perú',
      descripcion: 'Emergencias y peligro inmediato. Si temes por tu vida o la de tus hijos, llama ahora.',
      telefono: '105',
      accion: 'Llamar al 105',
      urgente: true
    },
    {
      nombre: 'Línea 100',
      descripcion:
        'Orientación, contención emocional e información sobre violencia familiar y sexual. Servicio del Ministerio de la Mujer.',
      telefono: '100',
      accion: 'Llamar al 100'
    },
    {
      nombre: 'Centros Emergencia Mujer (CEM)',
      descripcion:
        'Atención psicológica, legal y social para víctimas de violencia. Busca el centro más cercano a tu domicilio.',
      accion: 'Ver sitio del MIMP',
      enlace: 'https://www.gob.pe/mimp'
    }
  ];

  protected readonly pasos = [
    'Ve a un lugar seguro desde donde puedas hablar sin que te escuchen.',
    'Ten a la mano tu dirección o referencias del lugar donde estás.',
    'Cuenta lo que ocurre con tus palabras; puedes pedir que repitan lo que no entiendas.',
    'Pregunta qué medidas de protección existen para tu caso y cómo solicitarlas.'
  ];
}
