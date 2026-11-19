import { HttpErrorResponse } from '@angular/common/http';

/**
 * Convierte un error de HttpClient en un mensaje claro para la usuaria.
 * El backend responde { fecha, estado, error, mensaje, ruta } (ver ErrorResponse en Spring).
 */
export function mensajeDeError(error: unknown, porDefecto = 'No se pudo completar la operación.'): string {
  if (!(error instanceof HttpErrorResponse)) {
    return porDefecto;
  }
  if (error.status === 0) {
    return 'No se pudo conectar con el servidor. Revisa tu conexión e inténtalo de nuevo.';
  }
  if (error.status === 401) {
    return 'Usuario o contraseña incorrectos.';
  }
  if (error.status === 403) {
    return 'No tienes permiso para hacer esto.';
  }
  const mensajeServidor = (error.error as { mensaje?: string } | null)?.mensaje;
  return mensajeServidor || porDefecto;
}
