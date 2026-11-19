import { Component, inject, OnInit } from '@angular/core';
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

  evaluaciones: ResumenEvaluacionResponse[] = [];

  cargando = true;
  eliminandoId: number | null = null;
  evaluacionSeleccionada: number | null = null;
  error = '';
  mensaje = '';

  ngOnInit(): void {
    this.cargarHistorial();
  }

  cargarHistorial(): void {
    this.cargando = true;
    this.error = '';

    this.evaluacionService.listar().subscribe({
      next: evaluaciones => {
        this.evaluaciones = evaluaciones;
        this.cargando = false;
      },
      error: err => {
        this.cargando = false;
        this.error =
          err?.error?.mensaje ||
          err?.error?.message ||
          'No se pudo cargar el historial de evaluaciones.';
      }
    });
  }

  confirmarEliminacion(id: number): void {
    this.evaluacionSeleccionada = id;
    this.mensaje = '';
  }

  cancelarEliminacion(): void {
    this.evaluacionSeleccionada = null;
  }

  eliminarEvaluacion(): void {
    const id = this.evaluacionSeleccionada;

    if (id === null || this.eliminandoId !== null) {
      return;
    }

    this.eliminandoId = id;
    this.error = '';

    this.evaluacionService.eliminar(id).subscribe({
      next: () => {
        this.evaluaciones = this.evaluaciones.filter(
          evaluacion => evaluacion.id !== id
        );

        this.evaluacionSeleccionada = null;
        this.eliminandoId = null;
        this.mensaje = 'La evaluación se eliminó correctamente.';
      },
      error: err => {
        this.eliminandoId = null;
        this.error =
          err?.error?.mensaje ||
          err?.error?.message ||
          'No se pudo eliminar la evaluación. Inténtalo nuevamente.';
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
