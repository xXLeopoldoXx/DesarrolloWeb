import { ValidatorFn } from '@angular/forms';

export const noBlanco: ValidatorFn = control =>
  typeof control.value === 'string' && control.value.trim() ? null : { required: true };

export const PATRON_TELEFONO = /^\+?[0-9]{6,15}$/;
