import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { API_URL } from '../../core/config';
import { AsistenteService } from './asistente.service';

describe('AsistenteService', () => {
  let servicio: AsistenteService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    servicio = TestBed.inject(AsistenteService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('enviar hace POST /asistente/mensajes con el mensaje y el historial', () => {
    let respuesta = '';
    servicio.enviar('¿y después?', [{ rol: 'usuaria', texto: 'hola' }]).subscribe(r => (respuesta = r.respuesta));

    const peticion = http.expectOne(`${API_URL}/asistente/mensajes`);
    expect(peticion.request.method).toBe('POST');
    expect(peticion.request.body).toEqual({ mensaje: '¿y después?', historial: [{ rol: 'usuaria', texto: 'hola' }] });
    peticion.flush({ respuesta: 'Claro', emergencia: false });

    expect(respuesta).toBe('Claro');
  });

  it('estado hace GET /asistente/estado', () => {
    let disponible = false;
    servicio.estado().subscribe(e => (disponible = e.disponible));

    const peticion = http.expectOne(`${API_URL}/asistente/estado`);
    expect(peticion.request.method).toBe('GET');
    peticion.flush({ disponible: true });

    expect(disponible).toBe(true);
  });
});
