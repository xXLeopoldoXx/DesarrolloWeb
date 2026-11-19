import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../../core/config';

export type RolMensaje = 'usuaria' | 'asistente';

/** Un mensaje de la conversación tal como lo espera el backend. */
export interface Turno {
  rol: RolMensaje;
  texto: string;
}

export interface RespuestaAsistente {
  respuesta: string;
  /** true si el mensaje indicó peligro: la respuesta es un texto fijo y se muestran los botones de llamada. */
  emergencia: boolean;
}

export interface EstadoAsistente {
  disponible: boolean;
}

@Injectable({ providedIn: 'root' })
export class AsistenteService {
  private readonly http = inject(HttpClient);

  /** El backend no guarda la conversación: se envían los últimos mensajes como contexto. */
  enviar(mensaje: string, historial: Turno[]): Observable<RespuestaAsistente> {
    return this.http.post<RespuestaAsistente>(`${API_URL}/asistente/mensajes`, { mensaje, historial });
  }

  estado(): Observable<EstadoAsistente> {
    return this.http.get<EstadoAsistente>(`${API_URL}/asistente/estado`);
  }
}
