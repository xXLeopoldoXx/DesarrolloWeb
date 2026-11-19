import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ProductoCard } from '../../components/producto-card/producto-card';
import { Producto } from '../../models/producto';
import { PRODUCTOS } from '../../data/productos.mock';

@Component({
  selector: 'app-productos',
  standalone: true,
  imports: [ProductoCard],
  templateUrl: './productos.html',
  styleUrl: './productos.scss'
})
export class Productos {

  productos = signal<Producto[]>(PRODUCTOS);

  favoritos = signal(0);

  constructor(private router: Router) {}

  irDetalle(id: number): void {
    this.router.navigate(['/productos', id]);
  }

  registrarFavorito(id: number): void {
    console.log('Producto agregado a favoritos:', id);
    this.favoritos.update(total => total + 1);
  }
}
