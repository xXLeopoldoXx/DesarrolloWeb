import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API } from './api';

export interface NuevoProductoDatos {
  nombre: string;
  categoria: string;
  precio: number;
  stock: number;
}

export interface Producto extends NuevoProductoDatos {
  id: number;
}

export interface ResumenAdmin {
  totalProductos: number;
  productosStockBajo: number;
  totalMovimientos: number;
  estado: string;
}

@Injectable({ providedIn: 'root' })
export class ProductoService {
  private readonly http = inject(HttpClient);
  private readonly productosUrl = API + '/productos';

  listar(): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.productosUrl);
  }

  porPrecio(min: number, max: number): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.productosUrl + '/precio', { params: { min, max } });
  }

  porId(id: number): Observable<Producto> {
    return this.http.get<Producto>(this.productosUrl + '/' + id);
  }

  crear(datos: NuevoProductoDatos): Observable<Producto> {
    return this.http.post<Producto>(this.productosUrl, datos);
  }

  registrarSalida(id: number, cantidad: number): Observable<Producto> {
    return this.http.post<Producto>(API + '/inventario/' + id + '/salida', null, {
      params: { cantidad },
    });
  }

  resumen(): Observable<ResumenAdmin> {
    return this.http.get<ResumenAdmin>(API + '/admin/resumen');
  }
}
