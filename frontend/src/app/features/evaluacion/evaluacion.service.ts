
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { API_URL } from '../../core/config';

import {
  RespuestaItem,
  ResultadoResponse,
  ResumenEvaluacionResponse
} from './evaluacion.models';

import { AutoevaluacionRequest } from './evaluacion.models';

@Injectable({
  providedIn: 'root'
})
export class EvaluacionService {

  private readonly http = inject(HttpClient);

  private readonly endpoint = `${API_URL}/mi/evaluaciones`;

  /**
   * Crear una nueva evaluación.
   */
  crear(respuestas: RespuestaItem[]): Observable<ResultadoResponse> {
    const request: AutoevaluacionRequest = { respuestas };

    return this.http.post<ResultadoResponse>(
      this.endpoint,
      request
    );
  }

  /**
   * Listar las evaluaciones de la usuaria autenticada.
   */
  listar(): Observable<ResumenEvaluacionResponse[]> {
    return this.http.get<ResumenEvaluacionResponse[]>(
      this.endpoint
    );
  }

  /**
   * Obtener el detalle de una evaluación.
   */
  obtener(id: number): Observable<ResultadoResponse> {
    return this.http.get<ResultadoResponse>(
      `${this.endpoint}/${id}`
    );
  }

  /**
   * Eliminar una evaluación.
   */
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.endpoint}/${id}`
    );
  }
}
