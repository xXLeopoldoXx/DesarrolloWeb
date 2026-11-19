import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';

import { Publicacion } from '../comunidad.service';
import { TarjetaPublicacion } from './tarjeta-publicacion';

const PUBLICACION: Publicacion = {
  id: 3,
  seudonimo: 'Luna',
  texto: 'Hola a todas',
  fecha: '2026-11-19T10:00:00',
  esMia: false,
  totalComentarios: 2,
  reacciones: { conteos: { ABRAZO: 1, FUERZA: 0, IDENTIFICO: 0 }, mias: [] }
};

describe('TarjetaPublicacion', () => {
  let fixture: ComponentFixture<TarjetaPublicacion>;
  let http: HttpTestingController;
  let el: HTMLElement;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    http = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(TarjetaPublicacion);
    fixture.componentRef.setInput('publicacion', PUBLICACION);
    fixture.detectChanges();
    el = fixture.nativeElement;
  });

  afterEach(() => http.verify());

  const boton = (tipo: string) => el.querySelector(`.reaccion[data-tipo="${tipo}"]`) as HTMLButtonElement;

  it('muestra el texto, quién lo escribió y los totales de reacciones', () => {
    expect(el.querySelector('.publicacion__texto')?.textContent).toContain('Hola a todas');
    expect(el.querySelector('.publicacion__quien strong')?.textContent).toContain('Luna');
    expect(boton('ABRAZO').textContent).toContain('1');
    expect(boton('ABRAZO').getAttribute('aria-pressed')).toBe('false');
  });

  it('reaccionar cambia al instante y se confirma con el servidor', () => {
    boton('FUERZA').click();
    fixture.detectChanges();

    // Cambio inmediato, antes de que responda el servidor
    expect(boton('FUERZA').getAttribute('aria-pressed')).toBe('true');
    expect(boton('FUERZA').textContent).toContain('1');

    const peticion = http.expectOne(r => r.url.endsWith('/comunidad/publicaciones/3/reacciones'));
    expect(peticion.request.method).toBe('POST');
    expect(peticion.request.body).toEqual({ tipo: 'FUERZA' });
    peticion.flush({ conteos: { ABRAZO: 1, FUERZA: 5, IDENTIFICO: 0 }, mias: ['FUERZA'] });
    fixture.detectChanges();

    expect(boton('FUERZA').textContent).toContain('5');
  });

  it('si falla el servidor, la reacción vuelve a como estaba y avisa', () => {
    boton('ABRAZO').click();
    fixture.detectChanges();

    http.expectOne(r => r.url.endsWith('/reacciones')).flush('error', { status: 500, statusText: 'Error' });
    fixture.detectChanges();

    expect(boton('ABRAZO').getAttribute('aria-pressed')).toBe('false');
    expect(boton('ABRAZO').textContent).toContain('1');
    expect(el.querySelector('.aviso--error')?.textContent).toContain('No se pudo guardar tu reacción');
  });

  it('abre los comentarios, los carga una sola vez y permite comentar', () => {
    const alternar = el.querySelector('.acciones .enlace') as HTMLButtonElement;
    expect(alternar.textContent).toContain('Ver comentarios (2)');

    alternar.click();
    fixture.detectChanges();
    http.expectOne(r => r.method === 'GET' && r.url.endsWith('/3/comentarios')).flush([
      { id: 1, publicacionId: 3, seudonimo: 'Eva', texto: 'Ánimo', fecha: '2026-11-19T10:05:00', esMia: false },
      { id: 2, publicacionId: 3, seudonimo: 'Yo', texto: 'Gracias', fecha: '2026-11-19T10:06:00', esMia: true }
    ]);
    fixture.detectChanges();
    expect(el.querySelectorAll('.comentario').length).toBe(2);
    // «Responder» solo en comentarios de otras
    expect(el.querySelectorAll('.comentario .enlace--chico').length).toBe(1);

    // Cerrar y abrir de nuevo no vuelve a pedirlos
    alternar.click();
    fixture.detectChanges();
    alternar.click();
    fixture.detectChanges();

    const caja = el.querySelector('textarea[aria-label="Escribir un comentario"]') as HTMLTextAreaElement;
    caja.value = 'Estoy contigo';
    caja.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    (el.querySelector('.nuevo .boton') as HTMLButtonElement).click();

    const creado = http.expectOne(r => r.method === 'POST' && r.url.endsWith('/3/comentarios'));
    expect(creado.request.body).toEqual({ texto: 'Estoy contigo' });
    creado.flush({ id: 9, publicacionId: 3, seudonimo: 'Yo', texto: 'Estoy contigo', fecha: '2026-11-19T10:10:00', esMia: true });
    fixture.detectChanges();

    expect(el.querySelectorAll('.comentario').length).toBe(3);
    expect(caja.value).toBe('');
  });

  it('«Responder» deja escrita la mención de la persona', async () => {
    (el.querySelector('.acciones .enlace') as HTMLButtonElement).click();
    fixture.detectChanges();
    http.expectOne(r => r.method === 'GET').flush([
      { id: 1, publicacionId: 3, seudonimo: 'Eva', texto: 'Ánimo', fecha: '2026-11-19T10:05:00', esMia: false }
    ]);
    fixture.detectChanges();

    (el.querySelector('.comentario .enlace--chico') as HTMLButtonElement).click();
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    const caja = el.querySelector('textarea[aria-label="Escribir un comentario"]') as HTMLTextAreaElement;
    expect(caja.value).toBe('@Eva ');
  });

  it('no ofrece reportar la propia publicación', () => {
    fixture.componentRef.setInput('publicacion', { ...PUBLICACION, esMia: true });
    fixture.detectChanges();

    expect(el.textContent).not.toContain('Reportar');
    expect(el.querySelector('.insignia')?.textContent).toContain('Tú');
  });
});
