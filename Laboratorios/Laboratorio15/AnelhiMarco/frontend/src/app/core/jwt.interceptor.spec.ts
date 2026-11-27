import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { jwtInterceptor } from './jwt.interceptor';

describe('Sesion JWT', () => {
  let http: HttpTestingController;
  let auth: AuthService;
  let client: HttpClient;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptors([jwtInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    http = TestBed.inject(HttpTestingController);
    auth = TestBed.inject(AuthService);
    client = TestBed.inject(HttpClient);
  });

  afterEach(() => http.verify());

  function login(): void {
    auth.iniciarSesion({ username: 'admin', password: 'valor-ficticio' }).subscribe();
    const solicitud = http.expectOne({ method: 'POST', url: '/api/auth/login' });
    expect(solicitud.request.headers.has('Authorization')).toBe(false);
    solicitud.flush({ accessToken: 'token-ficticio' });
    const perfil = http.expectOne('/api/auth/me');
    expect(perfil.request.headers.get('Authorization')).toBe('Bearer token-ficticio');
    perfil.flush({ username: 'admin', roles: ['ROLE_ADMIN'] });
  }

  it('consulta el perfil y limita el JWT a las URLs relativas de la API', () => {
    login();
    expect(auth.esAdmin()).toBe(true);
    client.get('/api/productos').subscribe();
    const local = http.expectOne('/api/productos');
    expect(local.request.headers.get('Authorization')).toBe('Bearer token-ficticio');
    local.flush([]);
    client.get('https://example.invalid/api/productos').subscribe();
    const externa = http.expectOne('https://example.invalid/api/productos');
    expect(externa.request.headers.has('Authorization')).toBe(false);
    externa.flush([]);
  });

  it('borra token y perfil ante un 401 y vuelve al inicio de sesión', () => {
    login();
    const navegar = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    client.get('/api/productos').subscribe({ error: () => undefined });
    http.expectOne('/api/productos').flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(auth.accessToken()).toBeNull();
    expect(auth.perfil()).toBeNull();
    expect(navegar).toHaveBeenCalledWith(['/login'], { queryParams: { estado: 'sesion-vencida' } });
  });
});
