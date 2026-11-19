import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';

import { Contactos } from './contactos';

describe('Red de confianza', () => {
  let fixture: ComponentFixture<Contactos>;
  let http: HttpTestingController;
  const contactos = '/api/mi/contactos';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [Contactos],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    });
    fixture = TestBed.createComponent(Contactos);
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => http.verify());

  it('editar y eliminar usa el id del contacto y nunca envía el id de la usuaria', () => {
    http.expectOne(contactos)
      .flush([{ id: 3, nombre: 'Contacto de prueba', telefono: '000000001', relacion: null }]);
    fixture.detectChanges();

    const editar: HTMLButtonElement = fixture.nativeElement.querySelector('[aria-label^="Editar contacto"]');
    editar.click();
    fixture.detectChanges();

    const nombre: HTMLInputElement = fixture.nativeElement.querySelector('#nombre-contacto');
    expect(fixture.nativeElement.ownerDocument.activeElement).toBe(nombre);
    nombre.value = 'Nombre actualizado';
    nombre.dispatchEvent(new Event('input'));
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit', { cancelable: true }));

    const actualizar = http.expectOne({ method: 'PUT', url: contactos + '/3' });
    expect(actualizar.request.body).toEqual({ nombre: 'Nombre actualizado', telefono: '000000001', relacion: '' });
    actualizar.flush({ id: 3, nombre: 'Nombre actualizado', telefono: '000000001', relacion: null });
    fixture.detectChanges();

    const eliminar: HTMLButtonElement = fixture.nativeElement.querySelector('[aria-label^="Eliminar contacto"]');
    eliminar.click();
    http.expectOne({ method: 'DELETE', url: contactos + '/3' }).flush(null, { status: 204, statusText: 'No Content' });
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Aún no tienes contactos guardados');
  });

  it('valida el teléfono antes de enviar', () => {
    http.expectOne(contactos).flush([]);
    fixture.detectChanges();

    const nombre: HTMLInputElement = fixture.nativeElement.querySelector('#nombre-contacto');
    const telefono: HTMLInputElement = fixture.nativeElement.querySelector('#telefono-contacto');
    nombre.value = 'Contacto ficticio';
    nombre.dispatchEvent(new Event('input'));
    telefono.value = 'abc';
    telefono.dispatchEvent(new Event('input'));
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit', { cancelable: true }));
    fixture.detectChanges();

    http.expectNone(contactos);
    expect(telefono.getAttribute('aria-invalid')).toBe('true');
    expect(fixture.nativeElement.querySelector('#ayuda-telefono').getAttribute('role')).toBe('alert');
  });
});
