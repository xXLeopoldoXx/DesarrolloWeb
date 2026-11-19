import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormControl,
  FormRecord,
  ReactiveFormsModule
} from '@angular/forms';
import { Router } from '@angular/router';

import { PREGUNTAS } from '../preguntas';
import { EvaluacionService } from '../evaluacion.service';
import {
  Respuesta,
  RespuestaItem
} from '../evaluacion.models';

@Component({
  selector: 'app-cuestionario',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './cuestionario.html',
  styleUrl: './cuestionario.scss'
})
export class Cuestionario {

  private readonly evaluacionService = inject(EvaluacionService);
  private readonly router = inject(Router);

  readonly preguntas = PREGUNTAS;

  readonly opciones: { valor: Respuesta; texto: string }[] = [
    { valor: 'SI', texto: 'Sí' },
    { valor: 'NO', texto: 'No' },
    { valor: 'NO_SEGURA', texto: 'No estoy segura' }
  ];

  readonly formulario =
    new FormRecord<FormControl<Respuesta | null>>({});

  enviando = false;
  mostrarConfirmacion = false;
  error = '';

  constructor() {
    for (const pregunta of this.preguntas) {
      this.formulario.addControl(
        pregunta.factor,
        new FormControl<Respuesta | null>(null)
      );
    }
  }

  // Obtener las respuestas seleccionadas.
  get respuestas(): RespuestaItem[] {
    return this.preguntas.map(pregunta => {
      const respuesta =
        this.formulario.controls[pregunta.factor].value;

      if (respuesta === null) {
        throw new Error(
          `Falta responder: ${pregunta.factor}`
        );
      }

      return {
        factor: pregunta.factor,
        respuesta
      };
    });
  }

  // Contar las respuestas que necesitan confirmación.
  get respuestasPendientes(): number {
    return this.preguntas.filter(
      pregunta =>
        this.formulario.controls[pregunta.factor].value ===
        'NO_SEGURA'
    ).length;
  }

  // Calcular el porcentaje de avance.
  get progreso(): number {
    const contestadas = Object.values(
      this.formulario.controls
    ).filter(control => control.value !== null).length;

    return Math.round(
      (contestadas / this.preguntas.length) * 100
    );
  }

  // Validar y enviar el cuestionario.
  enviar(): void {
    this.error = '';

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();

      this.error =
        'Responde todas las preguntas antes de continuar.';

      return;
    }

    if (
      this.respuestasPendientes > 0 &&
      !this.mostrarConfirmacion
    ) {
      this.mostrarConfirmacion = true;
      return;
    }

    this.crearEvaluacion();
  }

  // Confirmar las respuestas no seguras.
  confirmarRespuestas(): void {
    this.mostrarConfirmacion = false;
    this.crearEvaluacion();
  }

  // Regresar a editar las respuestas.
  editarRespuestas(): void {
    this.mostrarConfirmacion = false;
  }

  // Enviar la evaluación al backend.
  private crearEvaluacion(): void {
    this.enviando = true;
    this.error = '';

    this.evaluacionService.crear(this.respuestas).subscribe({
      next: resultado => {
        this.enviando = false;

        this.router.navigate([
          '/evaluacion/resultados',
          resultado.id
        ]);
      },

      error: err => {
        this.enviando = false;

        this.error =
          err?.error?.mensaje ||
          err?.error?.message ||
          'No se pudo guardar la evaluación. Inténtalo nuevamente.';
      }
    });
  }
}

