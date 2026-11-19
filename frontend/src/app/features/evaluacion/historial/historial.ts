import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { EvaluacionService } from '../evaluacion.service';
import { ResumenEvaluacionResponse } from '../evaluacion.models';

@Component({
  selector: 'app-historial',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './historial.html',
  styleUrl: './historial.scss'
})
export class Historial implements OnInit {

  private readonly evaluacionService = inject(EvaluacionService);

  // Signals: la app no usa zone.js, y un campo normal cambiado dentro de un subscribe no repinta la pantalla.
  readonly evaluaciones = signal<ResumenEvaluacionResponse[]>([]);

  readonly cargando = signal(true);
  readonly eliminandoId = signal<number | null>(null);
  readonly evaluacionSeleccionada = signal<number | null>(null);
  readonly error = signal('');
  readonly mensaje = signal('');

  ngOnInit(): void {
    this.cargarHistorial();
  }

  cargarHistorial(): void {
    this.cargando.set(true);
    this.error.set('');

    this.evaluacionService.listar().subscribe({
      next: evaluaciones => {
        this.evaluaciones.set(evaluaciones);
        this.cargando.set(false);
      },
      error: err => {
        this.cargando.set(false);
        this.error.set(
          err?.error?.mensaje ||
          err?.error?.message ||
          'No se pudo cargar el historial de evaluaciones.'
        );
      }
    });
  }

  confirmarEliminacion(id: number): void {
    this.evaluacionSeleccionada.set(id);
    this.mensaje.set('');
  }

  cancelarEliminacion(): void {
    this.evaluacionSeleccionada.set(null);
  }

  eliminarEvaluacion(): void {
    const id = this.evaluacionSeleccionada();

    if (id === null || this.eliminandoId() !== null) {
      return;
    }

    this.eliminandoId.set(id);
    this.error.set('');

    this.evaluacionService.eliminar(id).subscribe({
      next: () => {
        this.evaluaciones.update(lista =>
          lista.filter(evaluacion => evaluacion.id !== id)
        );

        this.evaluacionSeleccionada.set(null);
        this.eliminandoId.set(null);
        this.mensaje.set('La evaluación se eliminó correctamente.');
      },
      error: err => {
        this.eliminandoId.set(null);
        this.error.set(
          err?.error?.mensaje ||
          err?.error?.message ||
          'No se pudo eliminar la evaluación. Inténtalo nuevamente.'
        );
      }
    });
  }

  getClaseNivel(nivel: string): string {
    const valor = nivel
      ?.trim()
      .toUpperCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '');

    switch (valor) {
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
}
