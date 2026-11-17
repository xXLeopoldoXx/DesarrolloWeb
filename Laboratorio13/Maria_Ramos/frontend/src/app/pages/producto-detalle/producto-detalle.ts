import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CurrencyPipe } from '@angular/common';
import { PRODUCTOS } from '../../data/productos.mock';

@Component({
  selector: 'app-producto-detalle',
  standalone: true,
  imports: [RouterLink, CurrencyPipe],
  templateUrl: './producto-detalle.html',
  styleUrl: './producto-detalle.scss'
})
export class ProductoDetalle {

  private route = inject(ActivatedRoute);

  private id = Number(this.route.snapshot.paramMap.get('id'));

  producto = PRODUCTOS.find(p => p.id === this.id);
}