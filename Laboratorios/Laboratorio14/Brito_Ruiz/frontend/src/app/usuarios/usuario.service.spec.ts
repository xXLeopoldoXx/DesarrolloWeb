import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { UsuarioService } from './usuario.service';
import { Usuario } from './usuario.model';

describe('UsuarioService', () => {
  const apiUrl = 'https://jsonplaceholder.typicode.com/users';
  let service: UsuarioService;
  let httpMock: HttpTestingController;

  const usuario: Usuario = {
    id: 1,
    name: 'Ana Pérez',
    username: 'aperez',
    email: 'ana@correo.com',
    phone: '987654321',
    website: 'ana.dev'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(UsuarioService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('obtenerTodos hace GET /users', () => {
    let resultado: Usuario[] = [];
    service.obtenerTodos().subscribe(usuarios => (resultado = usuarios));

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    req.flush([usuario]);

    expect(resultado).toEqual([usuario]);
  });

  it('crear hace POST /users con el cuerpo recibido', () => {
    const { id: _id, ...entrada } = usuario;
    service.crear(entrada).subscribe();

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(entrada);
    req.flush({ ...entrada, id: 11 });
  });

  it('actualizarParcial hace PATCH /users/:id', () => {
    service.actualizarParcial(1, { email: 'nuevo@correo.com' }).subscribe();

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({ email: 'nuevo@correo.com' });
    req.flush({ ...usuario, email: 'nuevo@correo.com' });
  });

  it('eliminar hace DELETE /users/:id', () => {
    service.eliminar(1).subscribe();

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });
});
