import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { Icono } from '../../../shared/icono/icono';
import { PREGUNTAS } from '../preguntas';
import { EvaluacionService } from '../evaluacion.service';
import { Factor, Respuesta, RespuestaItem } from '../evaluacion.models';

type Fase = 'intro' | 'preguntas' | 'revision';

interface Opcion {
  valor: Respuesta;
  texto: string;
  detalle: string;
}

/**
 * Cuestionario paso a paso: una pregunta por pantalla, con avance automático al elegir,
 * barra de progreso, posibilidad de volver atrás y una revisión final antes de enviar.
 * El estado vive en signals (la app no usa zone.js).
 */
@Component({
  selector: 'app-cuestionario',
  imports: [RouterLink, Icono],
  templateUrl: './cuestionario.html',
  styleUrl: './cuestionario.scss'
})
export class Cuestionario {
  private readonly evaluacionService = inject(EvaluacionService);
  private readonly router = inject(Router);

  readonly preguntas = PREGUNTAS;
  readonly total = PREGUNTAS.length;

  readonly opciones: Opcion[] = [
    { valor: 'SI', texto: 'Sí', detalle: 'Ha ocurrido o está ocurriendo' },
    { valor: 'NO', texto: 'No', detalle: 'No ha ocurrido' },
    { valor: 'NO_SEGURA', texto: 'No estoy segura', detalle: 'Prefiero confirmarlo más adelante' }
  ];

  readonly fase = signal<Fase>('intro');
  readonly indice = signal(0);
  readonly direccion = signal<'adelante' | 'atras'>('adelante');
  readonly respuestas = signal<Partial<Record<Factor, Respuesta>>>({});

  readonly enviando = signal(false);
  readonly mostrarConfirmacion = signal(false);
  readonly error = signal('');

  readonly pregunta = computed(() => this.preguntas[this.indice()]);
  /** Respuesta ya elegida para la pregunta que se está viendo (o null). */
  readonly actual = computed(() => this.respuestas()[this.pregunta().factor] ?? null);
  readonly contestadas = computed(() => Object.keys(this.respuestas()).length);
  readonly progreso = computed(() => Math.round((this.contestadas() / this.total) * 100));
  readonly pendientes = computed(
    () => Object.values(this.respuestas()).filter(valor => valor === 'NO_SEGURA').length
  );
  readonly completa = computed(() => this.contestadas() === this.total);

  private temporizador: ReturnType<typeof setTimeout> | null = null;

  constructor() {
    inject(DestroyRef).onDestroy(() => this.cancelarAvance());
  }

  empezar(): void {
    this.direccion.set('adelante');
    this.indice.set(0);
    this.fase.set('preguntas');
  }

  /** Guarda la respuesta y, tras un instante (para que se vea la selección), pasa a la siguiente. */
  elegir(valor: Respuesta): void {
    const factor = this.pregunta().factor;
    this.respuestas.update(actuales => ({ ...actuales, [factor]: valor }));
    this.error.set('');
    this.cancelarAvance();
    this.temporizador = setTimeout(() => this.siguiente(), 420);
  }

  siguiente(): void {
    this.cancelarAvance();
    this.direccion.set('adelante');
    if (this.indice() < this.total - 1) {
      this.indice.update(valor => valor + 1);
    } else {
      this.fase.set('revision');
    }
  }

  atras(): void {
    this.cancelarAvance();
    this.direccion.set('atras');
    if (this.fase() === 'revision') {
      this.indice.set(this.total - 1);
      this.fase.set('preguntas');
    } else if (this.indice() > 0) {
      this.indice.update(valor => valor - 1);
    } else {
      this.fase.set('intro');
    }
  }

  /** Desde la revisión: volver a una pregunta concreta para cambiar la respuesta. */
  editar(posicion: number): void {
    this.cancelarAvance();
    this.direccion.set('atras');
    this.mostrarConfirmacion.set(false);
    this.indice.set(posicion);
    this.fase.set('preguntas');
  }

  enviar(): void {
    this.error.set('');

    if (!this.completa()) {
      this.error.set('Responde todas las preguntas antes de continuar.');
      return;
    }

    if (this.pendientes() > 0 && !this.mostrarConfirmacion()) {
      this.mostrarConfirmacion.set(true);
      return;
    }

    this.crearEvaluacion();
  }

  confirmarRespuestas(): void {
    this.mostrarConfirmacion.set(false);
    this.crearEvaluacion();
  }

  editarRespuestas(): void {
    this.mostrarConfirmacion.set(false);
  }

  textoRespuesta(factor: Factor): string {
    const valor = this.respuestas()[factor];
    return this.opciones.find(opcion => opcion.valor === valor)?.texto ?? 'Sin responder';
  }

  private cancelarAvance(): void {
    if (this.temporizador !== null) {
      clearTimeout(this.temporizador);
      this.temporizador = null;
    }
  }

  private lista(): RespuestaItem[] {
    const actuales = this.respuestas();
    return this.preguntas.map(pregunta => ({
      factor: pregunta.factor,
      respuesta: actuales[pregunta.factor] as Respuesta
    }));
  }

  private crearEvaluacion(): void {
    this.enviando.set(true);
    this.error.set('');

    this.evaluacionService.crear(this.lista()).subscribe({
      next: resultado => {
        this.enviando.set(false);
        this.router.navigate(['/evaluacion/resultados', resultado.id]);
      },
      error: err => {
        this.enviando.set(false);
        this.error.set(
          err?.error?.mensaje ||
            err?.error?.message ||
            'No se pudo guardar la evaluación. Inténtalo nuevamente.'
        );
      }
    });
  }
}
