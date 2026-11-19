import { Component } from '@angular/core';

import { PRODUCTOS } from '../../data/productos.mock';

interface CategoriaResumen {
  nombre: string;
  cantidad: number;
}

@Component({
  selector: 'app-categorias',
  templateUrl: './categorias.html',
  styleUrl: './categorias.scss'
})
export class Categorias {
  // Categorías únicas de PRODUCTOS, con la cantidad de productos por categoría.
  readonly categorias: CategoriaResumen[] = [
    ...new Set(PRODUCTOS.map(producto => producto.categoria))
  ].map(nombre => ({
    nombre,
    cantidad: PRODUCTOS.filter(producto => producto.categoria === nombre).length
  }));
}
