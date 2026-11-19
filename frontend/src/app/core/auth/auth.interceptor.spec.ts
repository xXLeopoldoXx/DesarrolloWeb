import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { API_URL } from '../config';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from './auth.service';
import { jwtFalso } from './jwt-falso.testing';

describe('authInterceptor', () => {
  let http: HttpClient;
  let controlador: HttpTestingController;
  let auth: AuthService;

  beforeEach(() => {
    sessionStorage.clear();
    sessionStorage.setItem('riesgo.token', jwtFalso(['ROLE_USER'], 600));

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    http = TestBed.inject(HttpClient);
    controlador = TestBed.inject(HttpTestingController);
    auth = TestBed.inject(AuthService);
  });

  afterEach(() => controlador.verify());

  it('agrega el Bearer a las peticiones hacia la API propia', () => {
    http.get(`${API_URL}/mi/perfil`).subscribe();

    const peticion = controlador.expectOne(`${API_URL}/mi/perfil`);
    expect(peticion.request.headers.get('Authorization')).toMatch(/^Bearer /);
    peticion.flush({});
  });

  it('NO envía el token a otros dominios', () => {
    http.get('https://otro-sitio.com/datos').subscribe();

    const peticion = controlador.expectOne('https://otro-sitio.com/datos');
    expect(peticion.request.headers.has('Authorization')).toBe(false);
    peticion.flush({});
  });

  it('cierra la sesión cuando la API responde 401', () => {
    const cerrar = vi.spyOn(auth, 'cerrarSesion').mockImplementation(() => undefined);

    http.get(`${API_URL}/mi/perfil`).subscribe({ error: () => undefined });
    controlador.expectOne(`${API_URL}/mi/perfil`).flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(cerrar).toHaveBeenCalledTimes(1);
  });

  it('un 401 del login NO cierra sesión (son credenciales incorrectas)', () => {
    const cerrar = vi.spyOn(auth, 'cerrarSesion').mockImplementation(() => undefined);

    http.post(`${API_URL}/auth/login`, {}).subscribe({ error: () => undefined });
    controlador.expectOne(`${API_URL}/auth/login`).flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(cerrar).not.toHaveBeenCalled();
  });
});
