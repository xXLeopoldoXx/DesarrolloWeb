import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { catchError, Observable, switchMap, tap, throwError } from 'rxjs';
import { API } from './api';

interface Credenciales {
  username: string;
  password: string;
}

interface TokenRespuesta {
  accessToken: string;
}

interface Perfil {
  username: string;
  roles: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly token = signal<string | null>(null);
  private readonly usuaria = signal<Perfil | null>(null);

  readonly accessToken = this.token.asReadonly();
  readonly perfil = this.usuaria.asReadonly();
  readonly esAdmin = computed(() => this.perfil()?.roles.includes('ROLE_ADMIN') ?? false);

  iniciarSesion(credenciales: Credenciales): Observable<Perfil> {
    this.cerrarSesion();
    return this.http.post<TokenRespuesta>(API + '/auth/login', credenciales).pipe(
      switchMap(({ accessToken }) => {
        this.token.set(accessToken);
        return this.http.get<Perfil>(API + '/auth/me');
      }),
      tap((perfil) => this.usuaria.set(perfil)),
      catchError((error: unknown) => {
        this.cerrarSesion();
        return throwError(() => error);
      }),
    );
  }

  cerrarSesion(): void {
    this.token.set(null);
    this.usuaria.set(null);
  }
}
