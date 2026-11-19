import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { API_URL } from '../../core/config';
import { Accion, Contacto, ContactoSolicitud, EstadoBorradoCuenta, Lugar, LugarSolicitud } from './seguridad.models';

@Injectable({ providedIn: 'root' })
export class SeguridadService {
  private readonly http = inject(HttpClient);
  private readonly acciones = `${API_URL}/mi/plan/acciones`;
  private readonly lugares = `${API_URL}/mi/plan/lugares`;
  private readonly contactos = `${API_URL}/mi/contactos`;
  private readonly cuenta = `${API_URL}/mi/cuenta`;

  listarAcciones() {
    return this.http.get<Accion[]>(this.acciones);
  }

  crearAccion(texto: string) {
    return this.http.post<Accion>(this.acciones, { texto });
  }

  marcarAccion(id: number, completada: boolean) {
    return this.http.patch<Accion>(`${this.acciones}/${id}`, { completada });
  }

  eliminarAccion(id: number) {
    return this.http.delete<void>(`${this.acciones}/${id}`);
  }

  listarLugares() {
    return this.http.get<Lugar[]>(this.lugares);
  }

  crearLugar(lugar: LugarSolicitud) {
    return this.http.post<Lugar>(this.lugares, lugar);
  }

  eliminarLugar(id: number) {
    return this.http.delete<void>(`${this.lugares}/${id}`);
  }

  listarContactos() {
    return this.http.get<Contacto[]>(this.contactos);
  }

  crearContacto(contacto: ContactoSolicitud) {
    return this.http.post<Contacto>(this.contactos, contacto);
  }

  actualizarContacto(id: number, contacto: ContactoSolicitud) {
    return this.http.put<Contacto>(`${this.contactos}/${id}`, contacto);
  }

  eliminarContacto(id: number) {
    return this.http.delete<void>(`${this.contactos}/${id}`);
  }

  estadoBorradoCuenta() {
    return this.http.get<EstadoBorradoCuenta>(`${this.cuenta}/estado`);
  }

  eliminarCuenta() {
    return this.http.delete<void>(this.cuenta);
  }
}
