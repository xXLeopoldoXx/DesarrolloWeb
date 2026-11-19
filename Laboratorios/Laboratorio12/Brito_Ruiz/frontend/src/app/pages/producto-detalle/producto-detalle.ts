import { Component, inject } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PRODUCTOS } from '../../data/productos.mock';

@Component({
  selector: 'app-producto-detalle',
  imports: [CurrencyPipe, RouterLink],
  templateUrl: './producto-detalle.html',
  styleUrl: './producto-detalle.scss'
})
export class ProductoDetalle {
  private readonly route = inject(ActivatedRoute);

  // El parámetro llega como string: se convierte a number antes de buscar.
  readonly id = Number(this.route.snapshot.paramMap.get('id'));
  readonly producto = PRODUCTOS.find(item => item.id === this.id);
}
