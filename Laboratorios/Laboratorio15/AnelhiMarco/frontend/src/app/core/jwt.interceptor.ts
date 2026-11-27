import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { API } from './api';
import { AuthService } from './auth.service';

export const jwtInterceptor: HttpInterceptorFn = (request, next) => {
  if (!request.url.startsWith(API + '/') || request.url === API + '/auth/login') {
    return next(request);
  }

  const auth = inject(AuthService);
  const token = auth.accessToken();
  if (!token) {
    return next(request);
  }

  const router = inject(Router);
  return next(request.clone({ setHeaders: { Authorization: 'Bearer ' + token } })).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse && error.status === 401 && auth.accessToken() === token) {
        auth.cerrarSesion();
        if (request.url !== API + '/auth/me') {
          void router.navigate(['/login'], { queryParams: { estado: 'sesion-vencida' } });
        }
      }
      return throwError(() => error);
    }),
  );
};
