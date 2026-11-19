import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { describe, expect, it, beforeEach, afterEach } from 'vitest';

import { API_URL } from '../../core/config';
import { BienestarService } from './bienestar.service';
import { EntradaDiario, EntradaDiarioRequest, RecursoAutocuidado } from './bienestar.models';

describe('BienestarService', () => {
  let service: BienestarService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [BienestarService, provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(BienestarService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('listarDiario() llama a GET /api/mi/diario', () => {
    const mockEntradas: EntradaDiario[] = [
      {
        id: 1,
        usuarioId: 5,
        titulo: 'Día de paz',
        fecha: '2026-11-19T10:00:00',
        estadoAnimo: 4,
        emocion: 'Tranquila',
        contenido: 'Reflexión privada'
      }
    ];

    service.listarDiario().subscribe(entradas => {
      expect(entradas).toEqual(mockEntradas);
      expect(entradas.length).toBe(1);
    });

    const req = httpTesting.expectOne(`${API_URL}/mi/diario`);
    expect(req.request.method).toBe('GET');
    req.flush(mockEntradas);
  });

  it('crearEntrada() envía POST /api/mi/diario', () => {
    const peticion: EntradaDiarioRequest = {
      titulo: 'Nueva entrada',
      estadoAnimo: 5,
      emocion: 'Esperanza',
      contenido: 'Texto nuevo'
    };

    const mockRespuesta: EntradaDiario = {
      id: 10,
      usuarioId: 5,
      ...peticion,
      fecha: '2026-11-19T12:00:00'
    };

    service.crearEntrada(peticion).subscribe(res => {
      expect(res.id).toBe(10);
      expect(res.titulo).toBe('Nueva entrada');
    });

    const req = httpTesting.expectOne(`${API_URL}/mi/diario`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(peticion);
    req.flush(mockRespuesta);
  });

  it('listarRecursos() llama a GET /api/mi/bienestar/recursos con filtro opcional', () => {
    const mockRecursos: RecursoAutocuidado[] = [
      {
        id: 1,
        categoria: 'MINDFULNESS',
        titulo: 'Respiración 4-7-8',
        descripcion: 'Ejercicio de calma',
        tipo: 'EJERCICIO',
        enlaceOConsejo: 'Inhala 4s...'
      }
    ];

    service.listarRecursos('MINDFULNESS').subscribe(recursos => {
      expect(recursos.length).toBe(1);
      expect(recursos[0].categoria).toBe('MINDFULNESS');
    });

    const req = httpTesting.expectOne(`${API_URL}/mi/bienestar/recursos?categoria=MINDFULNESS`);
    expect(req.request.method).toBe('GET');
    req.flush(mockRecursos);
  });
});
