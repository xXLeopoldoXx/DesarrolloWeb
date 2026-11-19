import { TestBed } from '@angular/core/testing';
import { describe, expect, it } from 'vitest';

import { Ilustracion } from './ilustracion';

describe('Ilustracion', () => {
  function crear(nombre: 'apoyo' | 'flor' | 'hojas', etiqueta = '') {
    const fixture = TestBed.createComponent(Ilustracion);
    fixture.componentRef.setInput('nombre', nombre);
    fixture.componentRef.setInput('etiqueta', etiqueta);
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  it('dibuja cada ilustración como SVG', () => {
    for (const nombre of ['apoyo', 'flor', 'hojas'] as const) {
      expect(crear(nombre).querySelector('svg')).not.toBeNull();
    }
  });

  it('es decorativa (oculta a lectores de pantalla) si no tiene etiqueta', () => {
    const svg = crear('apoyo').querySelector('svg') as SVGElement;

    expect(svg.getAttribute('aria-hidden')).toBe('true');
    expect(svg.getAttribute('role')).toBeNull();
  });

  it('se anuncia como imagen cuando tiene etiqueta', () => {
    const svg = crear('apoyo', 'Dos mujeres abrazadas').querySelector('svg') as SVGElement;

    expect(svg.getAttribute('role')).toBe('img');
    expect(svg.getAttribute('aria-label')).toBe('Dos mujeres abrazadas');
    expect(svg.getAttribute('aria-hidden')).toBeNull();
  });
});
