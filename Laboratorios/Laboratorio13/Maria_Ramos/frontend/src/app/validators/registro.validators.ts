import {
  AbstractControl,
  ValidationErrors,
  ValidatorFn
} from '@angular/forms';

// Validador de edad mínima
export function edadMinimaValidator(minima: number): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {

    if (control.value === null || control.value === '') {
      return null;
    }

    const edad = Number(control.value);

    if (isNaN(edad) || edad < minima) {
      return {
        edadMinima: {
          minima: minima,
          actual: edad
        }
      };
    }

    return null;
  };
}

// Validador para comprobar que las contraseñas coincidan
export function passwordsIgualesValidator(
  control: AbstractControl
): ValidationErrors | null {

  const password = control.get('password')?.value;
  const confirmarPassword = control.get('confirmarPassword')?.value;

  if (!password || !confirmarPassword) {
    return null;
  }

  return password === confirmarPassword
    ? null
    : { passwordsNoCoinciden: true };
}
