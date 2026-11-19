import { TestBed } from '@angular/core/testing';
import { describe, expect, it } from 'vitest';

import { Respira } from './respira';

describe('Respira', () => {
  function crear() {
    const fixture = TestBed.createComponent(Respira);
    fixture.detectChanges();
    return fixture;
  }

  it('muestra la guía de respiración con el botón para pausar', () => {
    const el: HTMLElement = crear().nativeElement;

    expect(el.querySelector('h2')?.textContent).toContain('Un momento para ti');
    expect(el.querySelector('button')?.textContent?.trim()).toBe('Pausar');
    expect(el.querySelector('.respira__aro')?.getAttribute('aria-hidden')).toBe('true');
  });

  it('pausa y reanuda el movimiento y lo anuncia con aria-pressed', () => {
    const fixture = crear();
    const el: HTMLElement = fixture.nativeElement;
    const boton = el.querySelector('button') as HTMLButtonElement;

    boton.click();
    fixture.detectChanges();
    expect(boton.textContent?.trim()).toBe('Continuar');
    expect(boton.getAttribute('aria-pressed')).toBe('true');
    expect(el.querySelector('.respira--pausada')).not.toBeNull();

    boton.click();
    fixture.detectChanges();
    expect(boton.textContent?.trim()).toBe('Pausar');
    expect(el.querySelector('.respira--pausada')).toBeNull();
  });
});
