
import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  ComunidadService,
  Publicacion,
  Comentario
} from './comunidad.service';

@Component({
  selector: 'app-comunidad',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="comunidad">

      <header class="encabezado">
        <h1>Comunidad</h1>
        <p>
          Un espacio para compartir experiencias,
          encontrar apoyo y acompañarnos con respeto.
        </p>
      </header>

      <div class="publicar-card">
        <h2>Crear publicación</h2>

        <textarea
          [(ngModel)]="texto"
          maxlength="500"
          placeholder="¿Qué te gustaría compartir?"
          rows="4">
        </textarea>

        <p class="aviso">
          🔒 No publiques datos que te identifiquen,
          como direcciones, teléfonos o nombres completos.
        </p>

        <div class="acciones">
          <span>{{ texto.length }}/500</span>

          <button
            type="button"
            [disabled]="!texto.trim() || publicando"
            (click)="publicar()">
            {{ publicando ? 'Publicando...' : 'Publicar' }}
          </button>
        </div>
      </div>

      <div class="muro">
        <h2>Publicaciones recientes</h2>

        <p *ngIf="cargando" class="mensaje">
          Cargando publicaciones...
        </p>

        <p *ngIf="error" class="mensaje-error">
          {{ error }}
        </p>

        <div
          *ngIf="!cargando && !error && publicaciones.length === 0"
          class="estado-vacio">

          <h3>Bienvenida a la comunidad</h3>
          <p>
            Aún no hay publicaciones.
            ¡Comparte la primera!
          </p>
        </div>

        <div
          *ngFor="let publicacion of publicaciones"
          class="publicacion-card">

          <div class="publicacion-encabezado">
            <strong>
              {{ publicacion.seudonimo || 'Anónima' }}
            </strong>

            <small>
              {{ publicacion.fecha | date:'dd/MM/yyyy HH:mm' }}
            </small>
          </div>

          <p class="publicacion-texto">
            {{ publicacion.texto }}
          </p>

          
<!-- SECCIÓN PARA REPORTAR PUBLICACIONES -->
<div class="reportes">

  <button
    type="button"
    class="boton-reportar"
    (click)="formularioReporteAbierto[publicacion.id] =
      !formularioReporteAbierto[publicacion.id]">

    {{ formularioReporteAbierto[publicacion.id]
      ? 'Cancelar reporte'
      : 'Reportar publicación' }}

  </button>

  <div
    *ngIf="formularioReporteAbierto[publicacion.id]"
    class="formulario-reporte">

    <p>¿Por qué deseas reportar esta publicación?</p>

    <textarea
      [ngModel]="motivosReportes[publicacion.id] || ''"
      (ngModelChange)="motivosReportes[publicacion.id] = $event"
      maxlength="300"
      rows="3"
      placeholder="Describe el motivo del reporte...">
    </textarea>

    <div class="acciones">
      <span>
        {{ (motivosReportes[publicacion.id] || '').length }}/300
      </span>

      <button
        type="button"
        [disabled]="
          !(motivosReportes[publicacion.id] || '').trim() ||
          enviandoReportes[publicacion.id]
        "
        (click)="reportarPublicacion(publicacion.id)">

        {{ enviandoReportes[publicacion.id]
          ? 'Enviando...'
          : 'Enviar reporte' }}

      </button>
    </div>

  </div>

  <p
    *ngIf="erroresReportes[publicacion.id]"
    class="mensaje-error"
    role="alert">
    {{ erroresReportes[publicacion.id] }}
  </p>

  <p
    *ngIf="mensajesReportes[publicacion.id]"
    class="mensaje"
    role="status">
    {{ mensajesReportes[publicacion.id] }}
  </p>

</div>


          <!-- SECCIÓN DE COMENTARIOS -->
          <div class="comentarios">

            <h3>Comentarios</h3>

            <p
              *ngIf="cargandoComentarios[publicacion.id]"
              class="mensaje">
              Cargando comentarios...
            </p>

            <p
              *ngIf="erroresComentarios[publicacion.id]"
              class="mensaje-error">
              {{ erroresComentarios[publicacion.id] }}
            </p>

            <div
              *ngFor="let comentario of comentarios[publicacion.id] || []"
              class="comentario-card">

              <div class="comentario-encabezado">
                <strong>
                  {{ comentario.seudonimo || 'Anónima' }}
                </strong>

                <small>
                  {{ comentario.fecha | date:'dd/MM/yyyy HH:mm' }}
                </small>
              </div>

              <p class="comentario-texto">
                {{ comentario.texto }}
              </p>
            </div>

            <p
              *ngIf="
                !cargandoComentarios[publicacion.id] &&
                !erroresComentarios[publicacion.id] &&
                (comentarios[publicacion.id] || []).length === 0
              "
              class="mensaje">
              Aún no hay comentarios.
            </p>

            <textarea
              [ngModel]="textosComentarios[publicacion.id] || ''"
              (ngModelChange)="textosComentarios[publicacion.id] = $event"
              maxlength="500"
              rows="2"
              placeholder="Escribe un comentario respetuoso...">
            </textarea>

            <div class="acciones">
              <span>
                {{ (textosComentarios[publicacion.id] || '').length }}/500
              </span>

              <button
                type="button"
                [disabled]="
                  !(textosComentarios[publicacion.id] || '').trim() ||
                  publicandoComentarios[publicacion.id]
                "
                (click)="publicarComentario(publicacion.id)">

                {{
                  publicandoComentarios[publicacion.id]
                    ? 'Comentando...'
                    : 'Comentar'
                }}
              </button>
            </div>

          </div>

        </div>
      </div>

    </section>
  `,
  styles: [`
    .comunidad {
      max-width: 850px;
      margin: 0 auto;
      padding: 24px;
    }

    .encabezado h1 {
      color: #176b60;
      margin-bottom: 8px;
    }

    .encabezado p {
      color: #64748b;
      line-height: 1.6;
    }

    .publicar-card,
    .estado-vacio,
    .publicacion-card {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 16px;
      padding: 24px;
      margin-top: 24px;
    }

    h2 {
      font-size: 20px;
      margin-bottom: 16px;
    }

    textarea {
      width: 100%;
      box-sizing: border-box;
      padding: 14px;
      border: 1px solid #cbd5e1;
      border-radius: 10px;
      font: inherit;
      resize: vertical;
    }

    textarea:focus {
      outline: 2px solid #21877b;
    }

    .aviso {
      font-size: 13px;
      color: #64748b;
      line-height: 1.5;
    }

    .acciones {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 16px;
    }

    button {
      background: #21877b;
      color: white;
      border: none;
      border-radius: 8px;
      padding: 11px 24px;
      cursor: pointer;
      font-weight: 600;
    }

    button:disabled {
      background: #94a3b8;
      cursor: not-allowed;
    }

    .muro {
      margin-top: 32px;
    }

    .estado-vacio {
      text-align: center;
      color: #64748b;
    }

    .publicacion-card {
      margin-top: 16px;
    }

    .publicacion-encabezado,
    .comentario-encabezado {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 12px;
      flex-wrap: wrap;
    }

    .publicacion-encabezado strong,
    .comentario-encabezado strong {
      color: #176b60;
    }

    .publicacion-encabezado small,
    .comentario-encabezado small {
      color: #64748b;
    }

    .publicacion-texto {
      margin-top: 16px;
      color: #334155;
      line-height: 1.6;
      white-space: pre-wrap;
      overflow-wrap: anywhere;
    }

    .comentarios {
      margin-top: 24px;
      padding-top: 20px;
      border-top: 1px solid #e2e8f0;
    }

    .comentarios h3 {
      font-size: 16px;
      color: #176b60;
      margin-bottom: 16px;
    }

    .comentario-card {
      background: #f1f8f6;
      border-radius: 10px;
      padding: 14px;
      margin-bottom: 12px;
    }

    .comentario-texto {
      color: #334155;
      line-height: 1.5;
      white-space: pre-wrap;
      overflow-wrap: anywhere;
    }

    .comentarios textarea {
      margin-top: 12px;
    }

    .mensaje {
      color: #64748b;
    }

    .mensaje-error {
      color: #b91c1c;
      margin-top: 12px;
    }
  `]
})
export class Comunidad implements OnInit {

  private readonly comunidadService = inject(ComunidadService);
  private readonly cdr = inject(ChangeDetectorRef);
  texto = '';
  publicaciones: Publicacion[] = [];

  comentarios: Record<number, Comentario[]> = {};
  textosComentarios: Record<number, string> = {};
  cargandoComentarios: Record<number, boolean> = {};
  publicandoComentarios: Record<number, boolean> = {};
  erroresComentarios: Record<number, string> = {};


  // REPORTES DE PUBLICACIONES

  formularioReporteAbierto: Record<number, boolean> = {};

  motivosReportes: Record<number, string> = {};

  enviandoReportes: Record<number, boolean> = {};

  erroresReportes: Record<number, string> = {};

  mensajesReportes: Record<number, string> = {};


  cargando = false;
  publicando = false;
  error = '';

  ngOnInit(): void {
    this.cargarPublicaciones();
  }


cargarPublicaciones(): void {
  this.cargando = true;
  this.error = '';

  this.comunidadService.listarPublicaciones().subscribe({
    next: (datos) => {
      this.publicaciones = datos;
      this.cargando = false;

      this.cdr.markForCheck();

      datos.forEach(publicacion => {
        this.cargarComentarios(publicacion.id);
      });
    },
    error: () => {
      this.error = 'No se pudieron cargar las publicaciones.';
      this.cargando = false;

      this.cdr.markForCheck();
    }
  });
}



publicar(): void {
  const contenido = this.texto.trim();

  if (!contenido || contenido.length > 500 || this.publicando) {
    return;
  }

  this.publicando = true;
  this.error = '';

  this.cdr.markForCheck();

  this.comunidadService.crearPublicacion(contenido).subscribe({
    next: () => {
      this.texto = '';
      this.publicando = false;

      this.cdr.markForCheck();

      this.cargarPublicaciones();
    },
    error: () => {
      this.error = 'No se pudo guardar la publicación.';
      this.publicando = false;

      this.cdr.markForCheck();
    }
  });
}



cargarComentarios(publicacionId: number): void {
  this.cargandoComentarios[publicacionId] = true;
  this.erroresComentarios[publicacionId] = '';

  this.cdr.markForCheck();

  this.comunidadService.listarComentarios(publicacionId).subscribe({
    next: (datos) => {
      this.comentarios[publicacionId] = datos;
      this.cargandoComentarios[publicacionId] = false;

      this.cdr.markForCheck();
    },
    error: () => {
      this.erroresComentarios[publicacionId] =
        'No se pudieron cargar los comentarios.';

      this.cargandoComentarios[publicacionId] = false;

      this.cdr.markForCheck();
    }
  });
}


publicarComentario(publicacionId: number): void {
  const contenido =
    (this.textosComentarios[publicacionId] || '').trim();

  if (
    !contenido ||
    contenido.length > 500 ||
    this.publicandoComentarios[publicacionId]
  ) {
    return;
  }

  this.publicandoComentarios[publicacionId] = true;
  this.erroresComentarios[publicacionId] = '';

  this.cdr.markForCheck();

  this.comunidadService
    .crearComentario(publicacionId, contenido)
    .subscribe({
      next: () => {
        this.textosComentarios[publicacionId] = '';
        this.publicandoComentarios[publicacionId] = false;

        this.cdr.markForCheck();

        this.cargarComentarios(publicacionId);
      },
      error: () => {
        this.erroresComentarios[publicacionId] =
          'No se pudo guardar el comentario.';

        this.publicandoComentarios[publicacionId] = false;

        this.cdr.markForCheck();
      }
    });
    
}


reportarPublicacion(publicacionId: number): void {

  const motivo =
    (this.motivosReportes[publicacionId] || '').trim();

  if (
    !motivo ||
    motivo.length > 300 ||
    this.enviandoReportes[publicacionId]
  ) {
    return;
  }

  this.enviandoReportes[publicacionId] = true;
  this.erroresReportes[publicacionId] = '';
  this.mensajesReportes[publicacionId] = '';

  this.cdr.markForCheck();

  this.comunidadService
    .reportarPublicacion(publicacionId, motivo)
    .subscribe({
      next: () => {
        this.enviandoReportes[publicacionId] = false;
        this.formularioReporteAbierto[publicacionId] = false;
        this.motivosReportes[publicacionId] = '';

        this.mensajesReportes[publicacionId] =
          'Tu reporte fue enviado correctamente. Será revisado por moderación.';

        this.cdr.markForCheck();
      },
      error: (err) => {
        this.enviandoReportes[publicacionId] = false;

        if (err.status === 409) {
          this.erroresReportes[publicacionId] =
            'Ya reportaste esta publicación anteriormente.';
        } else {
          this.erroresReportes[publicacionId] =
            'No se pudo enviar el reporte. Inténtalo nuevamente.';
        }

        this.cdr.markForCheck();
      }
    });
}


}
