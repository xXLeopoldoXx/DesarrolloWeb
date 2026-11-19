import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

import { ProductoCard } from '../../components/producto-card/producto-card';
import { PRODUCTOS } from '../../data/productos.mock';

@Component({
  selector: 'app-productos',
  imports: [ProductoCard],
  templateUrl: './productos.html',
  styleUrl: './productos.scss'
})
export class Productos {
  private readonly router = inject(Router);

  // Precio a partir del cual un producto se marca como destacado (Reto B).
  readonly precioDestacado = 1000;

  readonly productos = signal(PRODUCTOS);
  readonly favoritos = signal(0);
  readonly comparacion = signal<number[]>([]);

  irDetalle(id: number): void {
    this.router.navigate(['/productos', id]);
  }

  registrarFavorito(id: number): void {
    console.log('Favorito recibido desde el hijo:', id);
    this.favoritos.update(valor => valor + 1);
  }

  // Reto C: el padre almacena los ids agregados a comparación (sin duplicados).
  registrarComparacion(id: number): void {
    this.comparacion.update(ids => (ids.includes(id) ? ids : [...ids, id]));
  }
}
