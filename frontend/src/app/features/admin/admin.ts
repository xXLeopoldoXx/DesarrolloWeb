
import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  ComunidadService,
  ReporteComunidad
} from '../comunidad/comunidad.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="moderacion">

      <h1>Panel de moderación</h1>

      <p class="descripcion">
        Revisa los reportes enviados por las usuarias
        y administra las publicaciones de la comunidad.
      </p>

      <button
        type="button"
        class="btn-actualizar"
        (click)="cargarReportes()">
        Actualizar reportes
      </button>

      <p *ngIf="cargando">
        Cargando reportes...
      </p>

      <p *ngIf="error" class="error">
        {{ error }}
      </p>

      <p *ngIf="!cargando && !error && reportes.length === 0">
        No existen reportes registrados.
      </p>

      <article
        class="reporte"
        *ngFor="let reporte of reportes">

        <div class="cabecera">
          <strong>Reporte #{{ reporte.id }}</strong>
          <span>{{ reporte.fecha | date:'dd/MM/yyyy HH:mm' }}</span>
        </div>

        <p>
          <strong>Publicación #{{ reporte.publicacionId }}</strong>
        </p>

        <p>
          <strong>Autora:</strong>
          {{ reporte.autorPublicacion }}
        </p>

        <div class="contenido">
          <strong>Contenido publicado:</strong>
          <p>{{ reporte.textoPublicacion }}</p>
        </div>

        <div class="motivo">
          <strong>Motivo del reporte:</strong>
          <p>{{ reporte.motivo }}</p>
        </div>

        <p
          *ngIf="mensajes[reporte.publicacionId]"
          class="exito">
          {{ mensajes[reporte.publicacionId] }}
        </p>

        <p
          *ngIf="errores[reporte.publicacionId]"
          class="error">
          {{ errores[reporte.publicacionId] }}
        </p>

        <button
          type="button"
          class="btn-ocultar"
          [disabled]="ocultando[reporte.publicacionId]"
          (click)="ocultarPublicacion(reporte.publicacionId)">

          {{
            ocultando[reporte.publicacionId]
              ? 'Ocultando...'
              : 'Ocultar publicación'
          }}
        </button>

      </article>

    </section>
  `,
  styles: [`
    .moderacion {
      max-width: 900px;
      margin: 0 auto;
      padding: 24px;
    }

    h1 {
      margin-bottom: 12px;
    }

    .descripcion {
      color: #555;
      margin-bottom: 20px;
    }

    .btn-actualizar {
      background: #2563eb;
      color: white;
      border: none;
      border-radius: 8px;
      padding: 10px 16px;
      cursor: pointer;
      margin-bottom: 20px;
    }

    .reporte {
      background: white;
      border: 1px solid #ddd;
      border-radius: 12px;
      padding: 20px;
      margin-bottom: 18px;
    }

    .cabecera {
      display: flex;
      justify-content: space-between;
      gap: 12px;
      margin-bottom: 12px;
    }

    .cabecera span {
      color: #666;
      font-size: 13px;
    }

    .contenido,
    .motivo {
      background: #f8fafc;
      padding: 12px;
      border-radius: 8px;
      margin: 12px 0;
      overflow-wrap: anywhere;
    }

    .contenido p,
    .motivo p {
      white-space: pre-wrap;
      margin-bottom: 0;
    }

    .btn-ocultar {
      background: #b91c1c;
      color: white;
      border: none;
      border-radius: 8px;
      padding: 10px 16px;
      cursor: pointer;
    }

    button:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .error {
      color: #b91c1c;
    }

    .exito {
      color: #15803d;
    }
  `]
})
export class Moderacion implements OnInit {

  private readonly comunidadService = inject(ComunidadService);
  private readonly cdr = inject(ChangeDetectorRef);

  reportes: ReporteComunidad[] = [];

  cargando = false;
  error = '';

  ocultando: Record<number, boolean> = {};
  mensajes: Record<number, string> = {};
  errores: Record<number, string> = {};

  ngOnInit(): void {
    this.cargarReportes();
  }

  cargarReportes(): void {

    this.cargando = true;
    this.error = '';

    this.comunidadService.listarReportes().subscribe({
      next: (datos) => {
        this.reportes = datos;
        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.cargando = false;

        if (err.status === 403) {
          this.error = 'No tienes permisos de administrador.';
        } else if (err.status === 401) {
          this.error = 'Tu sesión no está autenticada.';
        } else {
          this.error = 'No se pudieron cargar los reportes.';
        }

        this.cdr.markForCheck();
      }
    });
  }

  ocultarPublicacion(publicacionId: number): void {

    const confirmar = window.confirm(
      '¿Deseas ocultar esta publicación de la comunidad?'
    );

    if (!confirmar) {
      return;
    }

    this.ocultando[publicacionId] = true;
    this.errores[publicacionId] = '';
    this.mensajes[publicacionId] = '';

    this.comunidadService.ocultarPublicacion(publicacionId).subscribe({
      next: () => {
        this.ocultando[publicacionId] = false;
        this.mensajes[publicacionId] =
          'Publicación ocultada correctamente.';
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.ocultando[publicacionId] = false;

        if (err.status === 403) {
          this.errores[publicacionId] =
            'No tienes permisos para ocultar publicaciones.';
        } else {
          this.errores[publicacionId] =
            'No se pudo ocultar la publicación.';
        }

        this.cdr.markForCheck();
      }
    });
  }
}
