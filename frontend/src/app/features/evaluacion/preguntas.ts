import { Pregunta } from './evaluacion.models';

export const PREGUNTAS: Pregunta[] = [
  {
    factor: 'AMENAZA_MUERTE',
    texto: '¿Tu pareja o expareja te ha amenazado con quitarte la vida?',
    ayuda: 'Considera amenazas directas o expresiones que te hayan hecho temer por tu vida.'
  },
  {
    factor: 'ACCESO_ARMAS',
    texto: '¿Tu pareja o expareja tiene acceso a armas?',
    ayuda: 'Considera armas de fuego u otros objetos que pueda utilizar para amenazarte.'
  },
  {
    factor: 'ESTRANGULAMIENTO_PREVIO',
    texto: '¿Tu pareja o expareja ha intentado asfixiarte o estrangularte?',
    ayuda: 'Considera situaciones en las que haya presionado tu cuello o dificultado tu respiración.'
  },
  {
    factor: 'DENUNCIAS_PREVIAS',
    texto: '¿Has realizado denuncias anteriores por situaciones de violencia contra tu pareja o expareja?',
    ayuda: 'Considera denuncias o reportes anteriores relacionados con violencia.'
  },
  {
    factor: 'INCUMPLIMIENTO_MEDIDA_PROTECCION',
    texto: '¿Tu pareja o expareja ha incumplido alguna medida de protección?',
    ayuda: 'Por ejemplo, acercarse o comunicarse contigo cuando tenía prohibido hacerlo.'
  },
  {
    factor: 'ACOSO_O_PERSECUCION',
    texto: '¿Tu pareja o expareja te persigue, vigila o acosa?',
    ayuda: 'Considera llamadas insistentes, seguimiento o vigilancia no deseada.'
  },
  {
    factor: 'SEPARACION_RECIENTE',
    texto: '¿Te has separado recientemente de tu pareja o estás intentando terminar la relación?',
    ayuda: 'Considera una separación reciente o el intento de finalizar la relación.'
  },
  {
    factor: 'CONTROL_EXTREMO',
    texto: '¿Tu pareja o expareja controla excesivamente tus actividades, relaciones o decisiones?',
    ayuda: 'Considera restricciones sobre tus amistades, movimientos, comunicaciones o decisiones.'
  },
  {
    factor: 'CONSUMO_ALCOHOL_DROGAS_AGRESOR',
    texto: '¿Tu pareja o expareja consume alcohol o drogas de manera problemática?',
    ayuda: 'Considera situaciones de consumo que estén relacionadas con episodios de violencia.'
  }
];
