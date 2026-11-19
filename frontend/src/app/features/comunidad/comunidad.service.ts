
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { API_URL } from '../../core/config';

export interface Publicacion {
  id: number;
  seudonimo: string;
  texto: string;
  fecha: string;
}

export interface Comentario {
  id: number;
  publicacionId: number;
  seudonimo: string;
  texto: string;
  fecha: string;
}

@Injectable({
  providedIn: 'root'
})
export class ComunidadService {

  private readonly http = inject(HttpClient);

  private readonly url = `${API_URL}/comunidad/publicaciones`;

  // PUBLICACIONES

  listarPublicaciones(): Observable<Publicacion[]> {
    return this.http.get<Publicacion[]>(this.url);
  }

  crearPublicacion(texto: string): Observable<Publicacion> {
    return this.http.post<Publicacion>(this.url, { texto });
  }

  // COMENTARIOS

  listarComentarios(publicacionId: number): Observable<Comentario[]> {
    return this.http.get<Comentario[]>(
      `${this.url}/${publicacionId}/comentarios`
    );
  }

  crearComentario(
    publicacionId: number,
    texto: string
  ): Observable<Comentario> {
    return this.http.post<Comentario>(
      `${this.url}/${publicacionId}/comentarios`,
      { texto }
    );
  }
}
