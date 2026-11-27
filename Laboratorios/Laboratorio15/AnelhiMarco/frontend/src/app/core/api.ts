import { HttpErrorResponse } from '@angular/common/http';

export const API = '/api';

export function mensajeApi(error: unknown, fallback: string): string {
  if (!(error instanceof HttpErrorResponse)) {
    return fallback;
  }

  switch (error.status) {
    case 0:
    case 502:
    case 503:
    case 504:
      return 'No se pudo conectar con la API. Comprueba que Spring Boot está en ejecución y vuelve a intentarlo.';
    case 401:
      return 'Vuelve a iniciar sesión para continuar.';
    case 403:
      return 'Tu cuenta no tiene permiso para realizar esta operación.';
    case 404:
      return 'No se encontró el producto o el recurso solicitado.';
    case 400:
    case 409: {
      const mensaje: unknown = error.error?.message ?? error.error?.mensaje;
      return typeof mensaje === 'string' && mensaje ? mensaje : fallback;
    }
    default:
      return fallback;
  }
}
