import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-summary-panel',
  standalone: true,
  imports: [],
  templateUrl: './summary-panel.html',
  styleUrl: './summary-panel.scss'
})
export class SummaryPanel {
  @Input() totalProductos = 0;
  @Input() productosDisponibles = 0;
  @Input() productosAgotados = 0;
}
