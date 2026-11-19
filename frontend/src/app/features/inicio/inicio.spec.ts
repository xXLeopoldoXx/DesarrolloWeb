import { describe, expect, it } from 'vitest';

import { saludoSegunHora } from './inicio';

describe('saludoSegunHora', () => {
  it('saluda con «Buenos días» de 5:00 a 11:59', () => {
    expect(saludoSegunHora(5)).toBe('Buenos días');
    expect(saludoSegunHora(11)).toBe('Buenos días');
  });

  it('saluda con «Buenas tardes» de 12:00 a 18:59', () => {
    expect(saludoSegunHora(12)).toBe('Buenas tardes');
    expect(saludoSegunHora(18)).toBe('Buenas tardes');
  });

  it('saluda con «Buenas noches» de 19:00 a 4:59', () => {
    expect(saludoSegunHora(19)).toBe('Buenas noches');
    expect(saludoSegunHora(0)).toBe('Buenas noches');
    expect(saludoSegunHora(4)).toBe('Buenas noches');
  });
});
