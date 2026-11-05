import { Component, input, output } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { Producto } from '../../models/producto';

@Component({
  selector: 'app-producto-card',
  standalone: true,
  imports: [CurrencyPipe],
  templateUrl: './producto-card.html',
  styleUrl: './producto-card.scss'
})
export class ProductoCard {

  producto = input.required<Producto>();

  verDetalle = output<number>();

  favoritoSeleccionado = output<number>();

  mostrarDetalle(): void {
    this.verDetalle.emit(this.producto().id);
  }

  agregarFavorito(): void {
    this.favoritoSeleccionado.emit(this.producto().id);
  }
}
