export type Factor =
  | 'AMENAZA_MUERTE'
  | 'ACCESO_ARMAS'
  | 'ESTRANGULAMIENTO_PREVIO'
  | 'DENUNCIAS_PREVIAS'
  | 'INCUMPLIMIENTO_MEDIDA_PROTECCION'
  | 'ACOSO_O_PERSECUCION'
  | 'SEPARACION_RECIENTE'
  | 'CONTROL_EXTREMO'
  | 'CONSUMO_ALCOHOL_DROGAS_AGRESOR';

export type Respuesta = 'SI' | 'NO' | 'NO_SEGURA';

export interface RespuestaItem {
  factor: Factor;
  respuesta: Respuesta;
}

export interface AutoevaluacionRequest {
  respuestas: RespuestaItem[];
}

export interface ResultadoResponse {
  id: number;
  fecha: string;
  nivel: string;
  puntaje: number;
  alertaCritica: boolean;
  senales: string[];
  porConfirmar: string[];
}

export interface ResumenEvaluacionResponse {
  id: number;
  fecha: string;
  nivel: string;
}

export interface Pregunta {
  factor: Factor;
  texto: string;
  ayuda: string;
}
