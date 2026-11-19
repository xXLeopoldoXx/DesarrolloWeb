import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Icono } from '../../../shared/icono/icono';
import { EvaluacionService } from '../evaluacion.service';
import { ResultadoResponse } from '../evaluacion.models';
import { ESCALA_NIVELES, infoNivel } from '../evaluacion.niveles';

@Component({
  selector: 'app-resultados',
  imports: [DatePipe, RouterLink, Icono],
  templateUrl: './resultados.html',
  styleUrl: './resultados.scss'
})
export class Resultados implements OnInit {
  private readonly evaluacionService = inject(EvaluacionService);
  private readonly route = inject(ActivatedRoute);

  readonly escala = ESCALA_NIVELES;

  // Signals: la app no usa zone.js, y un campo normal cambiado dentro de un subscribe no repinta la pantalla.
  readonly resultado = signal<ResultadoResponse | null>(null);
  readonly cargando = signal(true);
  readonly error = signal('');

  readonly nivel = computed(() => infoNivel(this.resultado()?.nivel));

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
}
