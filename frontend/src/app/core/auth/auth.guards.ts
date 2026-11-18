import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';

import { AuthService } from './auth.service';

/** Solo entra quien tiene sesión válida. Si recargó la página, recupera su perfil. */
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.sesionValida()) {
    return router.createUrlTree(['/login']);
  }
  if (auth.perfil()) {
    return true;
  }
  return auth.cargarPerfil().pipe(
    map(() => true),
    catchError(() => {
      auth.limpiarSesion();
      return of(router.createUrlTree(['/login']));
    })
  );
};

/** Login y registro: si ya hay sesión, manda a Inicio. */
export const invitadaGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.sesionValida() ? router.createUrlTree(['/inicio']) : true;
};

/** Zonas de moderación: solo ROLE_ADMIN. (El backend también lo exige: esto es solo experiencia de uso.) */
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.esAdmin() ? true : router.createUrlTree(['/inicio']);
};
