import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

import { API_URL } from '../config';
import { AuthService } from './auth.service';

/**
 * Agrega "Authorization: Bearer <token>" a toda petición hacia nuestra API.
 * Si el backend responde 401 con una sesión abierta (token vencido o inválido), cierra la sesión.
 * Los módulos NO deben tocar el token: solo usar HttpClient con API_URL.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.token();

  const esNuestraApi = req.url.startsWith(API_URL);
  const esAutenticacion = req.url.startsWith(`${API_URL}/auth/`);

  const peticion =
    esNuestraApi && token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(peticion).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse && error.status === 401 && esNuestraApi && !esAutenticacion) {
        auth.cerrarSesion();
      }
      return throwError(() => error);
    })
  );
};
