import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Mismas reglas que RegistroRequest en el backend (si cambian allá, cambiarlas aquí).
 * Acepta mayúsculas porque AuthService.registro() convierte el usuario a minúsculas antes de enviarlo
 * (el backend solo admite minúsculas).
 */
export const PATRON_USUARIO = /^[a-z0-9._-]{4,40}$/i;
export const PATRON_CLAVE = /^(?=.*[A-Z])(?=.*\d).+$/;

/** Validador de grupo: las dos contraseñas deben coincidir. */
export const clavesIgualesValidator: ValidatorFn = (grupo: AbstractControl): ValidationErrors | null => {
  const clave = grupo.get('password')?.value;
  const confirmar = grupo.get('confirmarPassword')?.value;

  if (!clave || !confirmar) {
    return null;
  }
  return clave === confirmar ? null : { clavesNoCoinciden: true };
};
