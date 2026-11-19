import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { EvaluacionService } from '../evaluacion.service';
import { ResultadoResponse } from '../evaluacion.models';

@Component({
  selector: 'app-resultados',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './resultados.html',
  styleUrl: './resultados.scss'
})
export class Resultados implements OnInit {

  private readonly evaluacionService = inject(EvaluacionService);
  private readonly route = inject(ActivatedRoute);

  // Signals: la app no usa zone.js, y un campo normal cambiado dentro de un subscribe no repinta la pantalla.
  readonly resultado = signal<ResultadoResponse | null>(null);
  readonly cargando = signal(true);
  readonly error = signal('');

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(id) || id <= 0) {
      this.cargando.set(false);
      this.error.set('El identificador de la evaluación no es válido.');
      return;
    }

    this.cargarResultado(id);
  }

  private cargarResultado(id: number): void {
    this.cargando.set(true);
    this.error.set('');

    this.evaluacionService.obtener(id).subscribe({
      next: resultado => {
        this.resultado.set(resultado);
        this.cargando.set(false);
      },
      error: err => {
        this.cargando.set(false);
        this.error.set(
          err?.error?.mensaje ||
          err?.error?.message ||
          'No se pudo cargar el resultado de la evaluación.'
        );
      }
    });
  }

  get claseNivel(): string {
    const nivel = this.resultado()?.nivel
      ?.trim()
      .toUpperCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '');

    switch (nivel) {
      case 'ALTO':
      case 'CRITICO':
        return 'nivel-alto';

      case 'MEDIO':
      case 'MODERADO':
        return 'nivel-medio';

      case 'BAJO':
        return 'nivel-bajo';

      default:
        return 'nivel-desconocido';
    }
  }

  get nombreNivel(): string {
    return this.resultado()?.nivel || 'No disponible';
  }
}
