import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { finalize } from 'rxjs';

import { mensajeDeError } from '../../../core/http-errors';
import { Icono } from '../../../shared/icono/icono';
import { RecursoAutocuidado } from '../bienestar.models';
import { BienestarService } from '../bienestar.service';

interface CategoriaFiltro {
  clave: string;
  etiqueta: string;
}

@Component({
  selector: 'app-recursos',
  imports: [CommonModule, Icono],
  templateUrl: './recursos.html',
  styleUrl: './recursos.scss'
})
export class Recursos implements OnInit {
  private readonly bienestarService = inject(BienestarService);

  protected readonly cargando = signal(false);
  protected readonly error = signal('');
  protected readonly recursos = signal<RecursoAutocuidado[]>([]);
  protected readonly categoriaActiva = signal<string>('');

  protected readonly categorias: CategoriaFiltro[] = [
    { clave: '', etiqueta: 'Todas las áreas' },
    { clave: 'MINDFULNESS', etiqueta: 'Mindfulness y Calma' },
    { clave: 'EMOCIONAL', etiqueta: 'Emocional' },
    { clave: 'FISICO', etiqueta: 'Físico y Relajación' },
    { clave: 'SOCIAL', etiqueta: 'Red de Apoyo' },
    { clave: 'SEGURIDAD', etiqueta: 'Orientación y Seguridad' }
  ];

  ngOnInit(): void {
    this.cargarRecursos();
  }

  cargarRecursos(categoria: string = this.categoriaActiva()): void {
    this.cargando.set(true);
    this.error.set('');

    this.bienestarService
      .listarRecursos(categoria)
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: datos => this.recursos.set(datos),
        error: err => this.error.set(mensajeDeError(err, 'No se pudieron cargar los recursos de autocuidado.'))
      });
  }

  filtrarPorCategoria(clave: string): void {
    this.categoriaActiva.set(clave);
    this.cargarRecursos(clave);
  }

  esUrl(texto: string | null | undefined): boolean {
    if (!texto) return false;
    return texto.startsWith('http://') || texto.startsWith('https://');
  }
}
