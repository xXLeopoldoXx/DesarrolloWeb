import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, switchMap, tap } from 'rxjs';

import { API_URL } from '../config';
import { JwtClaims, Perfil, RegistroDatos, TokenResponse } from './auth.models';

const CLAVE_TOKEN = 'riesgo.token';

/** Lee el contenido del JWT sin librerías. NO valida la firma: eso lo hace el backend. */
export function decodificarJwt(token: string | null): JwtClaims | null {
  if (!token) {
    return null;
  }
  try {
    const carga = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(carga)) as JwtClaims;
  } catch {
    return null;
  }
}

/**
 * Sesión de la usuaria.
 * El token se guarda en sessionStorage (no en localStorage): se borra al cerrar la pestaña,
 * lo que protege a quien comparte dispositivo.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly _token = signal<string | null>(this.leerToken());

  readonly token = this._token.asReadonly();
  readonly perfil = signal<Perfil | null>(null);
  readonly roles = computed(() => decodificarJwt(this._token())?.roles ?? []);
  readonly esAdmin = computed(() => this.roles().includes('ROLE_ADMIN'));
  /** Nombre para saludar: seudónimo, o el usuario si la cuenta es anterior al seudónimo. */
  readonly nombreVisible = computed(() => {
    const perfil = this.perfil();
    return perfil?.seudonimo || perfil?.username || '';
  });

  login(username: string, password: string): Observable<Perfil> {
    return this.http
      .post<TokenResponse>(`${API_URL}/auth/login`, { username: username.trim().toLowerCase(), password })
      .pipe(
        tap(respuesta => this.guardarToken(respuesta.accessToken)),
        switchMap(() => this.cargarPerfil())
      );
  }

  registro(datos: RegistroDatos): Observable<Perfil> {
    const cuerpo: RegistroDatos = { ...datos, username: datos.username.trim().toLowerCase() };
    return this.http.post<TokenResponse>(`${API_URL}/auth/registro`, cuerpo).pipe(
      tap(respuesta => this.guardarToken(respuesta.accessToken)),
      switchMap(() => this.cargarPerfil())
    );
  }

  cargarPerfil(): Observable<Perfil> {
    return this.http.get<Perfil>(`${API_URL}/mi/perfil`).pipe(tap(perfil => this.perfil.set(perfil)));
  }

  /** true si hay un token que todavía no venció. Si venció, limpia la sesión. */
  sesionValida(): boolean {
    const claims = decodificarJwt(this._token());
    if (claims && claims.exp * 1000 > Date.now()) {
      return true;
    }
    this.limpiarSesion();
    return false;
  }

  cerrarSesion(): void {
    this.limpiarSesion();
    this.router.navigate(['/login']);
  }

  /** Borra token y perfil sin navegar (lo usa también la salida rápida). */
  limpiarSesion(): void {
    this._token.set(null);
    this.perfil.set(null);
    try {
      sessionStorage.removeItem(CLAVE_TOKEN);
    } catch {
      // sessionStorage puede no estar disponible (modo privado estricto): se ignora
    }
  }

  private guardarToken(token: string): void {
    this._token.set(token);
    try {
      sessionStorage.setItem(CLAVE_TOKEN, token);
    } catch {
      // Sin sessionStorage la sesión dura mientras no se recargue la página
    }
  }

  private leerToken(): string | null {
    try {
      return sessionStorage.getItem(CLAVE_TOKEN);
    } catch {
      return null;
    }
  }
}
