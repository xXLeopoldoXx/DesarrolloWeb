import { IconoNombre } from '../../shared/icono/icono';

export interface NavItem {
  ruta: string;
  etiqueta: string;
  /** Texto corto para las tarjetas de acceso rápido de Inicio */
  descripcion: string;
  icono: IconoNombre;
  /** Color de acento de la tarjeta de Inicio */
  tono: 'teal' | 'lila' | 'coral';
  soloAdmin?: boolean;
}

/** Menú principal (los mismos 6 módulos del boceto). El orden aquí es el orden en pantalla. */
export const NAV_ITEMS: NavItem[] = [
  {
    ruta: '/inicio',
    etiqueta: 'Inicio',
    descripcion: 'Tu espacio seguro y accesos rápidos.',
    icono: 'home',
    tono: 'teal'
  },
  {
    ruta: '/evaluacion',
    etiqueta: 'Mi evaluación',
    descripcion: 'Responde unas preguntas para identificar señales de alerta.',
    icono: 'clipboard',
    tono: 'lila'
  },
  {
    ruta: '/seguridad',
    etiqueta: 'Mi seguridad',
    descripcion: 'Tu plan de seguridad, red de confianza y privacidad.',
    icono: 'shield',
    tono: 'coral'
  },
  {
    ruta: '/ayuda',
    etiqueta: 'Ayuda',
    descripcion: 'Línea 100 y servicios oficiales de apoyo.',
    icono: 'phone',
    tono: 'teal'
  },
  {
    ruta: '/bienestar',
    etiqueta: 'Bienestar',
    descripcion: 'Tu diario personal y recursos de autocuidado.',
    icono: 'heart',
    tono: 'lila'
  },
  {
    ruta: '/comunidad',
    etiqueta: 'Comunidad',
    descripcion: 'Comparte y recibe apoyo de otras mujeres.',
    icono: 'users',
    tono: 'coral'
  },
  {
    ruta: '/admin',
    etiqueta: 'Moderación',
    descripcion: 'Revisión de reportes de la comunidad.',
    icono: 'shield',
    tono: 'teal',
    soloAdmin: true
  }
];
