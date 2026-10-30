
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AppHeader } from './shared/app-header/app-header';
import { AppFooter } from './shared/app-footer/app-footer';
import { SummaryPanel } from './features/summary-panel/summary-panel';

interface Producto {
  id: number;
  nombre: string;
  precio: number;
  stock: number;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [AppHeader, AppFooter, SummaryPanel, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly titulo = 'Catálogo de productos';

  protected busqueda = '';

  protected readonly mostrarStock = signal(true);

  protected readonly productos: Producto[] = [
    { id: 1, nombre: 'Laptop', precio: 3500, stock: 8 },
    { id: 2, nombre: 'Teclado mecánico', precio: 250, stock: 0 },
    { id: 3, nombre: 'Monitor de 27 pulgadas', precio: 980, stock: 4 }
  ];

  protected get productosDisponibles(): number {
    return this.productos.filter(producto => producto.stock > 0).length;
  }

  protected get productosAgotados(): number {
    return this.productos.filter(producto => producto.stock === 0).length;
  }

  protected alternarStock(): void {
    this.mostrarStock.update(valor => !valor);
  }
}



