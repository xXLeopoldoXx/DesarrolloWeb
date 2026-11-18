import { FormControl, FormGroup } from '@angular/forms';

import { clavesIgualesValidator, PATRON_CLAVE, PATRON_USUARIO } from './registro.validators';

describe('validadores de registro', () => {
  const grupo = (password: string, confirmar: string) =>
    new FormGroup({ password: new FormControl(password), confirmarPassword: new FormControl(confirmar) });

  it('clavesIguales: null si coinciden, error si no', () => {
    expect(clavesIgualesValidator(grupo('Clave1234', 'Clave1234'))).toBeNull();
    expect(clavesIgualesValidator(grupo('Clave1234', 'Otra1234'))).toEqual({ clavesNoCoinciden: true });
  });

  it('clavesIguales no se queja mientras falte alguna de las dos', () => {
    expect(clavesIgualesValidator(grupo('Clave1234', ''))).toBeNull();
  });

  it('el patrón de usuario acepta letras (también mayúsculas) y rechaza espacios o muy corto', () => {
    expect(PATRON_USUARIO.test('maria.l')).toBe(true);
    expect(PATRON_USUARIO.test('anita_nuevoUsuario01')).toBe(true);
    expect(PATRON_USUARIO.test('ma')).toBe(false);
    expect(PATRON_USUARIO.test('con espacio')).toBe(false);
    expect(PATRON_USUARIO.test('tilde-ñ')).toBe(false);
  });

  it('el patrón de clave exige mayúscula y número', () => {
    expect(PATRON_CLAVE.test('Clave1234')).toBe(true);
    expect(PATRON_CLAVE.test('clave1234')).toBe(false);
    expect(PATRON_CLAVE.test('ClaveSinNumero')).toBe(false);
  });
});
