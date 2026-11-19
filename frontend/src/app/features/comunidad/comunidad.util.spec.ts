import { describe, expect, it } from 'vitest';

import { haceCuanto, inicialDe, TONOS_AVATAR, tonoAvatar } from './comunidad.util';

describe('haceCuanto', () => {
  const ahora = new Date('2026-11-19T12:00:00');

  it('dice «justo ahora» durante el primer minuto y con fechas futuras', () => {
    expect(haceCuanto('2026-11-19T11:59:30', ahora)).toBe('justo ahora');
    expect(haceCuanto('2026-11-19T12:05:00', ahora)).toBe('justo ahora');
  });

  it('usa minutos, horas y días', () => {
    expect(haceCuanto('2026-11-19T11:55:00', ahora)).toBe('hace 5 min');
    expect(haceCuanto('2026-11-19T09:00:00', ahora)).toBe('hace 3 h');
    expect(haceCuanto('2026-11-18T12:00:00', ahora)).toBe('hace 1 día');
    expect(haceCuanto('2026-11-16T12:00:00', ahora)).toBe('hace 3 días');
  });

  it('muestra la fecha completa pasada una semana', () => {
    expect(haceCuanto('2026-11-01T10:00:00', ahora)).toBe('01/11/2026');
  });

  it('devuelve vacío si la fecha no es válida', () => {
    expect(haceCuanto('no es una fecha', ahora)).toBe('');
  });
});

describe('inicialDe', () => {
  it('toma la primera letra en mayúscula', () => {
    expect(inicialDe('luna')).toBe('L');
    expect(inicialDe('  ámbar')).toBe('Á');
  });

  it('devuelve «?» si no hay seudónimo', () => {
    expect(inicialDe('')).toBe('?');
    expect(inicialDe(null)).toBe('?');
    expect(inicialDe(undefined)).toBe('?');
  });
});

describe('tonoAvatar', () => {
  it('es estable para el mismo seudónimo, sin importar mayúsculas', () => {
    expect(tonoAvatar('Luna')).toBe(tonoAvatar('luna'));
    expect(tonoAvatar('Luna')).toBe(tonoAvatar('Luna'));
  });

  it('siempre cae dentro de los tonos disponibles', () => {
    for (const nombre of ['a', 'Luna', 'Anónima', 'x'.repeat(200), '', 'Ñandú']) {
      const tono = tonoAvatar(nombre);
      expect(tono).toBeGreaterThanOrEqual(0);
      expect(tono).toBeLessThan(TONOS_AVATAR);
    }
  });
});
