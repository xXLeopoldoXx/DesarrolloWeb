import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../../core/config';
import { EntradaDiario, EntradaDiarioRequest, RecursoAutocuidado } from './bienestar.models';

@Injectable({ providedIn: 'root' })
export class BienestarService {
  private readonly http = inject(HttpClient);

  listarDiario(): Observable<EntradaDiario[]> {
    return this.http.get<EntradaDiario[]>(`${API_URL}/mi/diario`);
  }

  obtenerEntrada(id: number): Observable<EntradaDiario> {
    return this.http.get<EntradaDiario>(`${API_URL}/mi/diario/${id}`);
  }

  crearEntrada(datos: EntradaDiarioRequest): Observable<EntradaDiario> {
    return this.http.post<EntradaDiario>(`${API_URL}/mi/diario`, datos);
  }

  actualizarEntrada(id: number, datos: EntradaDiarioRequest): Observable<EntradaDiario> {
    return this.http.put<EntradaDiario>(`${API_URL}/mi/diario/${id}`, datos);
  }

  eliminarEntrada(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/mi/diario/${id}`);
  }

  listarRecursos(categoria?: string): Observable<RecursoAutocuidado[]> {
    let params = new HttpParams();
    if (categoria && categoria.trim()) {
      params = params.set('categoria', categoria.trim());
    }
    return this.http.get<RecursoAutocuidado[]>(`${API_URL}/mi/bienestar/recursos`, { params });
  }
}
