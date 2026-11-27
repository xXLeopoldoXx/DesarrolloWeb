import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ProductoService } from './producto.service';

describe('ProductoService', () => {
  let api: ProductoService;
  let http: HttpTestingController;
  const producto = { id: 7, nombre: 'Monitor ficticio', categoria: 'Pruebas', precio: 450, stock: 10 };

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    api = TestBed.inject(ProductoService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('consulta el catálogo con GET y devuelve los productos', () => {
    api.listar().subscribe((items) => expect(items).toEqual([producto]));
    http.expectOne({ method: 'GET', url: '/api/productos' }).flush([producto]);
  });

  it('envía el rango de precios como parámetros de la consulta', () => {
    api.porPrecio(100, 1000).subscribe((items) => expect(items).toEqual([producto]));
    const req = http.expectOne({ method: 'GET', url: '/api/productos/precio?min=100&max=1000' });
    req.flush([producto]);
  });

  it('envía el producto nuevo en JSON', () => {
    const { id, ...datos } = producto;
    api.crear(datos).subscribe((item) => expect(item.id).toBe(id));
    const req = http.expectOne({ method: 'POST', url: '/api/productos' });
    expect(req.request.body).toEqual(datos);
    req.flush(producto, { status: 201, statusText: 'Created' });
  });

  it('envía la salida con ID y cantidad y recibe el stock actualizado', () => {
    api.registrarSalida(7, 2).subscribe((item) => expect(item.stock).toBe(8));
    const req = http.expectOne({ method: 'POST', url: '/api/inventario/7/salida?cantidad=2' });
    expect(req.request.body).toBeNull();
    req.flush({ ...producto, stock: 8 });
  });
});
