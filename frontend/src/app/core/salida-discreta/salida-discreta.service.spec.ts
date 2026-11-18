import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { SalidaDiscretaService } from './salida-discreta.service';

describe('SalidaDiscretaService', () => {
  let servicio: SalidaDiscretaService;
  let salir: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideRouter([]), provideHttpClient()] });
    servicio = TestBed.inject(SalidaDiscretaService);
    // No se puede navegar de verdad dentro de la prueba: se reemplaza salir()
    salir = vi.fn();
    servicio.salir = salir as unknown as () => void;
  });

  it('un solo Esc no sale', () => {
    servicio.registrarEscape(1000);
    expect(salir).not.toHaveBeenCalled();
  });

  it('dos Esc seguidos (menos de 800 ms) activan la salida rápida', () => {
    servicio.registrarEscape(1000);
    servicio.registrarEscape(1500);
    expect(salir).toHaveBeenCalledTimes(1);
  });

  it('dos Esc separados por mucho tiempo no salen', () => {
    servicio.registrarEscape(1000);
    servicio.registrarEscape(3000);
    expect(salir).not.toHaveBeenCalled();
  });
});
