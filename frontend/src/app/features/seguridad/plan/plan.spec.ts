import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';

import { Plan } from './plan';

describe('Plan de seguridad', () => {
  let fixture: ComponentFixture<Plan>;
  let http: HttpTestingController;
  const acciones = '/api/mi/plan/acciones';
  const lugares = '/api/mi/plan/lugares';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [Plan],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    });
    fixture = TestBed.createComponent(Plan);
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => http.verify());

  it('muestra la carga, el vacío y el reintento por separado', () => {
    expect(fixture.nativeElement.textContent).toContain('Cargando acciones');
    http.expectOne(lugares).flush([]);
    http.expectOne(acciones).flush({}, { status: 500, statusText: 'Error' });
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Aún no tienes lugares guardados');
    const reintentar: HTMLButtonElement = fixture.nativeElement.querySelector('section button[type=button]');
    reintentar.click();
    http.expectOne(acciones).flush([]);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Aún no tienes acciones');
  });

  it('envía el checklist y conserva el estado previo si falla el cambio', () => {
    http.expectOne(lugares).flush([]);
    http.expectOne(acciones).flush([{ id: 10, texto: 'Acción de prueba', completada: false }]);
    fixture.detectChanges();

    const casilla: HTMLInputElement = fixture.nativeElement.querySelector('input[type=checkbox]');
    casilla.click();
    const cambio = http.expectOne({ method: 'PATCH', url: acciones + '/10' });
    expect(cambio.request.body).toEqual({ completada: true });
    cambio.flush({}, { status: 500, statusText: 'Error' });
    fixture.detectChanges();

    expect(casilla.checked).toBe(false);
    expect(fixture.nativeElement.querySelector('[role=alert]')).not.toBeNull();
    casilla.click();
    http.expectOne({ method: 'PATCH', url: acciones + '/10' })
      .flush({ id: 10, texto: 'Acción de prueba', completada: true });
    fixture.detectChanges();
    expect(casilla.checked).toBe(true);
  });

  it('crea un lugar tras validar el formulario', () => {
    http.expectOne(acciones).flush([]);
    http.expectOne(lugares).flush([]);
    fixture.detectChanges();

    const form: HTMLFormElement = fixture.nativeElement.querySelector('section:nth-child(2) form');
    form.dispatchEvent(new Event('submit', { cancelable: true }));
    http.expectNone(lugares);

    const nombre: HTMLInputElement = fixture.nativeElement.querySelector('#nombre-lugar');
    nombre.value = 'Lugar de prueba';
    nombre.dispatchEvent(new Event('input'));
    form.dispatchEvent(new Event('submit', { cancelable: true }));

    const peticion = http.expectOne({ method: 'POST', url: lugares });
    expect(peticion.request.body).toEqual({ nombre: 'Lugar de prueba', referencia: '', nota: '' });
    peticion.flush({ id: 20, nombre: 'Lugar de prueba', referencia: null, nota: null });
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('li').textContent).toContain('Lugar de prueba');
  });
});
