import { Component, ElementRef, inject, input, linkedSignal, signal, viewChild } from '@angular/core';

import { Icono, IconoNombre } from '../../../shared/icono/icono';
import { Comentario, ComunidadService, Publicacion, Reacciones, TipoReaccion } from '../comunidad.service';
import { haceCuanto, inicialDe, tonoAvatar } from '../comunidad.util';

interface OpcionReaccion {
  tipo: TipoReaccion;
  etiqueta: string;
  icono: IconoNombre;
}

/** Las tres formas de acompañar. Solo apoyo: no existe «no me gusta». */
export const REACCIONES: OpcionReaccion[] = [
  { tipo: 'ABRAZO', etiqueta: 'Te abrazo', icono: 'heart' },
  { tipo: 'FUERZA', etiqueta: 'Eres fuerte', icono: 'sol' },
  { tipo: 'IDENTIFICO', etiqueta: 'Me identifico', icono: 'users' }
];

const MAX_COMENTARIO = 500;
const MAX_MOTIVO = 300;

/**
 * Una publicación del muro con sus interacciones: reacciones de apoyo (con cambio inmediato),
 * comentarios (se cargan al abrirlos) con «Responder», y reporte a moderación.
 * El estado vive en signals (la app no usa zone.js).
 */
@Component({
  selector: 'app-tarjeta-publicacion',
  imports: [Icono],
  templateUrl: './tarjeta-publicacion.html',
  styleUrl: './tarjeta-publicacion.scss'
})
export class TarjetaPublicacion {
  private readonly servicio = inject(ComunidadService);

  readonly publicacion = input.required<Publicacion>();

  readonly opciones = REACCIONES;
  readonly maxComentario = MAX_COMENTARIO;
  readonly maxMotivo = MAX_MOTIVO;

  // ----- Reacciones -----
  /** Estado local: parte de lo que trae la publicación y se actualiza al instante al reaccionar. */
  readonly reacciones = linkedSignal<Reacciones>(() => this.publicacion().reacciones);
  readonly pulso = signal<TipoReaccion | null>(null);
  readonly enCurso = signal<TipoReaccion | null>(null);
  readonly errorReaccion = signal('');

  // ----- Comentarios -----
  readonly totalComentarios = linkedSignal(() => this.publicacion().totalComentarios);
  readonly comentarios = signal<Comentario[] | null>(null);
  readonly abiertos = signal(false);
  readonly cargandoComentarios = signal(false);
  readonly errorComentarios = signal('');
  readonly textoComentario = signal('');
  readonly enviandoComentario = signal(false);

  private readonly caja = viewChild<ElementRef<HTMLTextAreaElement>>('caja');

  // ----- Reporte -----
  readonly reporteAbierto = signal(false);
  readonly motivo = signal('');
  readonly enviandoReporte = signal(false);
  readonly errorReporte = signal('');
  readonly mensajeReporte = signal('');

  protected inicial(seudonimo: string): string {
    return inicialDe(seudonimo);
  }

  protected tono(seudonimo: string): number {
    return tonoAvatar(seudonimo);
  }

  protected hace(fecha: string): string {
    return haceCuanto(fecha);
  }

  protected cantidad(tipo: TipoReaccion): number {
    return this.reacciones().conteos[tipo] ?? 0;
  }

  protected dada(tipo: TipoReaccion): boolean {
    return this.reacciones().mias.includes(tipo);
  }

  // =====================================
  // REACCIONES
  // =====================================

  /** Cambia al instante y confirma con el servidor; si falla, vuelve a como estaba. */
  reaccionar(tipo: TipoReaccion): void {
    if (this.enCurso() !== null) {
      return;
    }

    const previo = this.reacciones();
    const yaLaHabia = previo.mias.includes(tipo);
    this.errorReaccion.set('');
    this.enCurso.set(tipo);
    this.reacciones.set({
      conteos: { ...previo.conteos, [tipo]: Math.max(0, (previo.conteos[tipo] ?? 0) + (yaLaHabia ? -1 : 1)) },
      mias: yaLaHabia ? previo.mias.filter(valor => valor !== tipo) : [...previo.mias, tipo]
    });
    if (!yaLaHabia) {
      this.pulso.set(tipo);
      setTimeout(() => this.pulso.set(null), 600);
    }

    this.servicio.reaccionar(this.publicacion().id, tipo).subscribe({
      next: estado => {
        this.reacciones.set(estado);
        this.enCurso.set(null);
      },
      error: () => {
        this.reacciones.set(previo);
        this.enCurso.set(null);
        this.errorReaccion.set('No se pudo guardar tu reacción. Inténtalo de nuevo.');
      }
    });
  }

  // =====================================
  // COMENTARIOS
  // =====================================

  alternarComentarios(): void {
    const abrir = !this.abiertos();
    this.abiertos.set(abrir);
    if (abrir && this.comentarios() === null) {
      this.cargarComentarios();
    }
  }

  cargarComentarios(): void {
    this.cargandoComentarios.set(true);
    this.errorComentarios.set('');

    this.servicio.listarComentarios(this.publicacion().id).subscribe({
      next: lista => {
        this.comentarios.set(lista);
        this.totalComentarios.set(lista.length);
        this.cargandoComentarios.set(false);
      },
      error: () => {
        this.errorComentarios.set('No se pudieron cargar los comentarios.');
        this.cargandoComentarios.set(false);
      }
    });
  }

  escribirComentario(valor: string): void {
    this.textoComentario.set(valor);
  }

  /** «Responder»: deja escrita la mención y pone el cursor en la caja. */
  responder(comentario: Comentario): void {
    this.textoComentario.set(`@${comentario.seudonimo} `);
    queueMicrotask(() => {
      const caja = this.caja()?.nativeElement;
      caja?.focus();
      caja?.setSelectionRange(caja.value.length, caja.value.length);
    });
  }

  comentar(): void {
    const contenido = this.textoComentario().trim();
    if (!contenido || contenido.length > MAX_COMENTARIO || this.enviandoComentario()) {
      return;
    }

    this.enviandoComentario.set(true);
    this.errorComentarios.set('');

    this.servicio.crearComentario(this.publicacion().id, contenido).subscribe({
      next: nuevo => {
        this.comentarios.update(lista => [...(lista ?? []), nuevo]);
        this.totalComentarios.update(total => total + 1);
        this.textoComentario.set('');
        this.enviandoComentario.set(false);
      },
      error: () => {
        this.errorComentarios.set('No se pudo guardar el comentario.');
        this.enviandoComentario.set(false);
      }
    });
  }

  // =====================================
  // REPORTE
  // =====================================

  alternarReporte(): void {
    this.reporteAbierto.update(valor => !valor);
    this.errorReporte.set('');
  }

  escribirMotivo(valor: string): void {
    this.motivo.set(valor);
  }

  reportar(): void {
    const motivo = this.motivo().trim();
    if (!motivo || motivo.length > MAX_MOTIVO || this.enviandoReporte()) {
      return;
    }

    this.enviandoReporte.set(true);
    this.errorReporte.set('');
    this.mensajeReporte.set('');

    this.servicio.reportarPublicacion(this.publicacion().id, motivo).subscribe({
      next: () => {
        this.enviandoReporte.set(false);
        this.reporteAbierto.set(false);
        this.motivo.set('');
        this.mensajeReporte.set('Tu reporte fue enviado. Será revisado por moderación.');
      },
      error: err => {
        this.enviandoReporte.set(false);
        this.errorReporte.set(
          err?.status === 409
            ? 'Ya reportaste esta publicación anteriormente.'
            : 'No se pudo enviar el reporte. Inténtalo nuevamente.'
        );
      }
    });
  }
}
