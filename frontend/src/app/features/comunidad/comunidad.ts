import { Component, OnInit, computed, inject, signal } from '@angular/core';

import { AuthService } from '../../core/auth/auth.service';
import { ComunidadService, Publicacion } from './comunidad.service';
import { inicialDe, tonoAvatar } from './comunidad.util';
import { TarjetaPublicacion } from './tarjeta-publicacion/tarjeta-publicacion';

type Orden = 'recientes' | 'acompanadas';

const MAX_PUBLICACION = 500;

/**
 * Muro de la Comunidad: compartir, acompañar con reacciones y comentar, siempre con respeto.
 * El estado vive en signals (la app no usa zone.js).
 */
@Component({
  selector: 'app-comunidad',
  imports: [TarjetaPublicacion],
  templateUrl: './comunidad.html',
  styleUrl: './comunidad.scss'
})
export class Comunidad implements OnInit {
  private readonly comunidadService = inject(ComunidadService);
  protected readonly auth = inject(AuthService);

  readonly maxPublicacion = MAX_PUBLICACION;

  readonly publicaciones = signal<Publicacion[]>([]);
  readonly cargando = signal(false);
  readonly error = signal('');

  readonly texto = signal('');
  readonly publicando = signal(false);
  readonly errorPublicar = signal('');

  readonly orden = signal<Orden>('recientes');

  /** Orden «recientes» = el del servidor; «más acompañadas» = más reacciones y comentarios primero. */
  readonly visibles = computed(() => {
    const lista = this.publicaciones();
    if (this.orden() === 'recientes') {
      return lista;
    }
    const apoyo = (p: Publicacion) =>
      Object.values(p.reacciones.conteos).reduce((suma, n) => suma + n, 0) + p.totalComentarios;
    return [...lista].sort((a, b) => apoyo(b) - apoyo(a));
  });

  ngOnInit(): void {
    this.cargarPublicaciones();
  }

  protected inicialPropia(): string {
    return inicialDe(this.auth.nombreVisible());
  }

  protected tonoPropio(): number {
    return tonoAvatar(this.auth.nombreVisible());
  }

  cargarPublicaciones(): void {
    this.cargando.set(true);
    this.error.set('');

    this.comunidadService.listarPublicaciones().subscribe({
      next: datos => {
        this.publicaciones.set(datos);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar las publicaciones.');
        this.cargando.set(false);
      }
    });
  }

  escribir(valor: string): void {
    this.texto.set(valor);
  }

  publicar(): void {
    const contenido = this.texto().trim();

    if (!contenido || contenido.length > MAX_PUBLICACION || this.publicando()) {
      return;
    }

    this.publicando.set(true);
    this.errorPublicar.set('');

    this.comunidadService.crearPublicacion(contenido).subscribe({
      next: nueva => {
        // La nueva va arriba sin recargar todo el muro
        this.publicaciones.update(lista => [nueva, ...lista]);
        this.texto.set('');
        this.publicando.set(false);
      },
      error: () => {
        this.errorPublicar.set('No se pudo guardar la publicación.');
        this.publicando.set(false);
      }
    });
  }

  cambiarOrden(orden: Orden): void {
    this.orden.set(orden);
  }
}
