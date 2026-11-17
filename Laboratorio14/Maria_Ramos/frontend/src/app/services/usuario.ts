import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario, UsuarioEntrada } from '../models/usuario';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private http = inject(HttpClient);
  private apiUrl = 'https://jsonplaceholder.typicode.com/users';

  // Obtener todos los usuarios
  obtenerTodos(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.apiUrl);
  }

  // Obtener un usuario por ID
  obtenerPorId(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}/${id}`);
  }

  // Crear un usuario
  crear(usuario: UsuarioEntrada): Observable<Usuario> {
    return this.http.post<Usuario>(this.apiUrl, usuario);
  }

  // Actualizar un usuario completo
  actualizar(id: number, usuario: UsuarioEntrada): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/${id}`, usuario);
  }

  // Actualizar parcialmente un usuario
  actualizarParcial(
    id: number,
    datos: Partial<UsuarioEntrada>
  ): Observable<Usuario> {
    return this.http.patch<Usuario>(`${this.apiUrl}/${id}`, datos);
  }

  // Eliminar un usuario
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

