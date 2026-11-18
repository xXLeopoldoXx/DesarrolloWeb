import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';
import { NAV_ITEMS } from '../../core/layout/nav-items';
import { Icono } from '../../shared/icono/icono';

@Component({
  selector: 'app-inicio',
  imports: [RouterLink, Icono],
  templateUrl: './inicio.html',
  styleUrl: './inicio.scss'
})
export class Inicio {
  protected readonly auth = inject(AuthService);

  // Accesos rápidos: todos los módulos menos Inicio y Moderación
  protected readonly accesos = NAV_ITEMS.filter(item => item.ruta !== '/inicio' && !item.soloAdmin);

  protected readonly recomendaciones = [
    'Si necesitas salir de aquí de inmediato, usa «Salida rápida» o presiona Esc dos veces.',
    'Si compartes el dispositivo, usa una ventana privada o de incógnito y cierra la sesión al terminar.',
    'No compartas tu contraseña y evita usar tu nombre real como usuario o seudónimo.',
    'Si estás en peligro inmediato, llama al 105 (Policía). Para orientación, llama a la Línea 100.',
    'Anota fechas y hechos importantes: pueden servirte para pedir medidas de protección.'
  ];
}
