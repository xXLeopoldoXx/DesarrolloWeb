import { describe, expect, it } from 'vitest';

import { ESCALA_NIVELES, infoNivel } from './evaluacion.niveles';

describe('infoNivel', () => {
  it('reconoce los cuatro niveles del backend', () => {
    expect(infoNivel('BAJO')).toMatchObject({ clave: 'bajo', etiqueta: 'Bajo', posicion: 0 });
    expect(infoNivel('MEDIO')).toMatchObject({ clave: 'medio', etiqueta: 'Medio', posicion: 1 });
    expect(infoNivel('ALTO')).toMatchObject({ clave: 'alto', etiqueta: 'Alto', posicion: 2 });
    expect(infoNivel('MUY_ALTO')).toMatchObject({ clave: 'muy-alto', etiqueta: 'Muy alto', posicion: 3 });
  });

  it('tolera minúsculas, espacios, guiones y acentos', () => {
    expect(infoNivel(' muy alto ').clave).toBe('muy-alto');
    expect(infoNivel('muy-alto').clave).toBe('muy-alto');
    expect(infoNivel('Crítico').clave).toBe('muy-alto');
    expect(infoNivel('moderado').clave).toBe('medio');
  });

  it('devuelve «No disponible» si el nivel no se reconoce', () => {
    for (const valor of [null, undefined, '', 'XYZ']) {
      expect(infoNivel(valor)).toMatchObject({ clave: 'desconocido', etiqueta: 'No disponible', posicion: -1 });
    }
  });

  it('cada nivel reconocido trae un mensaje de acompañamiento que no promete ni predice', () => {
    for (const { clave } of ESCALA_NIVELES) {
      const info = infoNivel(clave.replace('-', '_'));
      expect(info.mensaje.length).toBeGreaterThan(20);
      expect(info.mensaje.toLowerCase()).not.toContain('probabilidad');
    }
  });
});
