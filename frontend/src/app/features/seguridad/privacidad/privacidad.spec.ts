import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

import { AuthService } from '../../../core/auth/auth.service';
import { Privacidad } from './privacidad';

describe('Privacidad', () => {
  let fixture: ComponentFixture<Privacidad>;
  let http: HttpTestingController;
  const cuenta = '/api/mi/cuenta';
  const cerrarSesion = vi.fn();

  function boton(texto: string): HTMLButtonElement {
    const botones: HTMLButtonElement[] = Array.from(fixture.nativeElement.querySelectorAll('button'));
    const encontrado = botones.find(item => item.textContent?.trim() === texto);
    if (!encontrado) throw new Error('No aparece el botón ' + texto);
    return encontrado;
  }

  beforeEach(() => {
    cerrarSesion.mockClear();
    TestBed.configureTestingModule({
      imports: [Privacidad],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: { cerrarSesion } },
      ],
    });
    fixture = TestBed.createComponent(Privacidad);
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => http.verify());

  it('deja el borrado deshabilitado cuando el backend informa que aún no está disponible', () => {
    http.expectOne(cuenta + '/estado').flush({ disponible: false });
    fixture.detectChanges();

    const eliminar = boton('Eliminar mis datos');
    expect(eliminar.disabled).toBe(true);
    eliminar.click();
    http.expectNone({ method: 'DELETE', url: cuenta });
    expect(cerrarSesion).not.toHaveBeenCalled();
  });

  it('permite cancelar y solo cierra sesión después de recibir 204', () => {
    http.expectOne(cuenta + '/estado').flush({ disponible: true });
    fixture.detectChanges();

    boton('Eliminar mis datos').click();
    fixture.detectChanges();
    boton('Cancelar').click();
    fixture.detectChanges();

    http.expectNone({ method: 'DELETE', url: cuenta });
    boton('Eliminar mis datos').click();
    fixture.detectChanges();
    boton('Eliminar definitivamente').click();
    const peticion = http.expectOne({ method: 'DELETE', url: cuenta });
    expect(cerrarSesion).not.toHaveBeenCalled();

    peticion.flush(null, { status: 204, statusText: 'No Content' });
    expect(cerrarSesion).toHaveBeenCalledOnce();
  });

  it('vuelve a bloquear la acción ante un 503 y conserva la sesión', () => {
    http.expectOne(cuenta + '/estado').flush({ disponible: true });
    fixture.detectChanges();

    boton('Eliminar mis datos').click();
    fixture.detectChanges();
    boton('Eliminar definitivamente').click();
    http.expectOne({ method: 'DELETE', url: cuenta }).flush({}, { status: 503, statusText: 'Unavailable' });
    fixture.detectChanges();

    expect(boton('Eliminar mis datos').disabled).toBe(true);
    expect(cerrarSesion).not.toHaveBeenCalled();
  });
});
