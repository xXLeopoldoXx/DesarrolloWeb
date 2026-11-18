import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { API_URL } from '../config';
import { AuthService, decodificarJwt } from './auth.service';
import { jwtFalso } from './jwt-falso.testing';

describe('AuthService', () => {
  let auth: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    });
    auth = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('decodificarJwt lee los roles y devuelve null si el token está mal formado', () => {
    expect(decodificarJwt(jwtFalso(['ROLE_USER'], 60))?.roles).toEqual(['ROLE_USER']);
    expect(decodificarJwt('no-es-un-jwt')).toBeNull();
    expect(decodificarJwt(null)).toBeNull();
  });

  it('login normaliza el usuario, guarda el token y carga el perfil', () => {
    let nombre = '';
    auth.login('  Maria.L ', 'Clave1234').subscribe(perfil => (nombre = perfil.seudonimo ?? ''));

    const peticionLogin = http.expectOne(`${API_URL}/auth/login`);
    expect(peticionLogin.request.body).toEqual({ username: 'maria.l', password: 'Clave1234' });
    peticionLogin.flush({ tokenType: 'Bearer', accessToken: jwtFalso(['ROLE_USER'], 600), expiresInSeconds: 600 });

    http.expectOne(`${API_URL}/mi/perfil`).flush({
      id: 1,
      username: 'maria.l',
      seudonimo: 'Luna',
      roles: ['ROLE_USER'],
    });

    expect(nombre).toBe('Luna');
    expect(auth.sesionValida()).toBe(true);
    expect(auth.nombreVisible()).toBe('Luna');
    expect(auth.esAdmin()).toBe(false);
    expect(sessionStorage.getItem('riesgo.token')).toBeTruthy();
  });

  it('esAdmin es true cuando el token trae ROLE_ADMIN', () => {
    auth.login('admin', 'Admin123*').subscribe();
    http
      .expectOne(`${API_URL}/auth/login`)
      .flush({ tokenType: 'Bearer', accessToken: jwtFalso(['ROLE_ADMIN', 'ROLE_USER'], 600), expiresInSeconds: 600 });
    http.expectOne(`${API_URL}/mi/perfil`).flush({ id: 2, username: 'admin', seudonimo: null, roles: [] });

    expect(auth.esAdmin()).toBe(true);
    expect(auth.nombreVisible()).toBe('admin');
  });

  it('sesionValida es false y limpia todo si el token ya venció', () => {
    sessionStorage.setItem('riesgo.token', jwtFalso(['ROLE_USER'], -10));
    const otraInstancia = TestBed.runInInjectionContext(() => new AuthService());

    expect(otraInstancia.sesionValida()).toBe(false);
    expect(otraInstancia.token()).toBeNull();
    expect(sessionStorage.getItem('riesgo.token')).toBeNull();
  });

  it('limpiarSesion borra token y perfil', () => {
    auth.login('maria.l', 'Clave1234').subscribe();
    http
      .expectOne(`${API_URL}/auth/login`)
      .flush({ tokenType: 'Bearer', accessToken: jwtFalso(['ROLE_USER'], 600), expiresInSeconds: 600 });
    http.expectOne(`${API_URL}/mi/perfil`).flush({ id: 1, username: 'maria.l', seudonimo: 'Luna', roles: [] });

    auth.limpiarSesion();

    expect(auth.token()).toBeNull();
    expect(auth.perfil()).toBeNull();
    expect(sessionStorage.getItem('riesgo.token')).toBeNull();
  });
});
