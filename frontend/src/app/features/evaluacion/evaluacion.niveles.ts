/**
 * Niveles de la autoevaluación (los devuelve el backend como BAJO, MEDIO, ALTO o MUY_ALTO).
 * El resultado es orientativo: nunca es una predicción ni una probabilidad.
 */
export type NivelClave = 'bajo' | 'medio' | 'alto' | 'muy-alto' | 'desconocido';

export interface InfoNivel {
  clave: NivelClave;
  etiqueta: string;
  /** Posición en la escala (0 = bajo … 3 = muy alto); -1 si el nivel no se reconoce. */
  posicion: number;
  /** Mensaje de acompañamiento, sin alarmar y sin afirmar nada sobre el futuro. */
  mensaje: string;
}

/** Los cuatro peldaños de la escala, en orden. */
export const ESCALA_NIVELES: { clave: NivelClave; etiqueta: string }[] = [
  { clave: 'bajo', etiqueta: 'Bajo' },
  { clave: 'medio', etiqueta: 'Medio' },
  { clave: 'alto', etiqueta: 'Alto' },
  { clave: 'muy-alto', etiqueta: 'Muy alto' }
];

const MENSAJES: Record<Exclude<NivelClave, 'desconocido'>, string> = {
  bajo: 'Por ahora vemos pocas señales de alerta. Si algo cambia, puedes volver a hacer la evaluación cuando quieras.',
  medio:
    'Hay señales que merecen atención. Hablar con alguien de confianza o llamar a la Línea 100 puede ayudarte a ver los siguientes pasos.',
  alto:
    'Hay varias señales de alerta importantes. Te recomendamos buscar orientación especializada y revisar tu plan de seguridad.',
  'muy-alto':
    'Hay señales de alerta muy importantes. Busca apoyo especializado cuanto antes y prepara tu plan de seguridad. No tienes que hacerlo sola.'
};

/** Convierte el texto del backend en información lista para mostrar (tolera acentos, espacios y guiones). */
export function infoNivel(nivel: string | null | undefined): InfoNivel {
  const normalizado = (nivel ?? '')
    .trim()
    .toUpperCase()
    .normalize('NFD')
    .replace(/[̀-ͯ]/g, '')
    .replace(/[\s-]+/g, '_');

  let clave: NivelClave;
  switch (normalizado) {
    case 'BAJO':
      clave = 'bajo';
      break;
    case 'MEDIO':
    case 'MODERADO':
      clave = 'medio';
      break;
    case 'ALTO':
      clave = 'alto';
      break;
    case 'MUY_ALTO':
    case 'CRITICO':
      clave = 'muy-alto';
      break;
    default:
      clave = 'desconocido';
  }

  if (clave === 'desconocido') {
    return { clave, etiqueta: 'No disponible', posicion: -1, mensaje: '' };
  }
  const posicion = ESCALA_NIVELES.findIndex(n => n.clave === clave);
  return { clave, etiqueta: ESCALA_NIVELES[posicion].etiqueta, posicion, mensaje: MENSAJES[clave] };
}
