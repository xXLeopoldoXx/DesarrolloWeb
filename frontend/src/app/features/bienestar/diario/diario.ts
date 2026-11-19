import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';

import { mensajeDeError } from '../../../core/http-errors';
import { Icono } from '../../../shared/icono/icono';
import { EntradaDiario } from '../bienestar.models';
import { BienestarService } from '../bienestar.service';

interface OpcionAnimo {
  valor: number;
  etiqueta: string;
  emoji: string;
}

@Component({
  selector: 'app-diario',
  imports: [CommonModule, ReactiveFormsModule, Icono],
  templateUrl: './diario.html',
  styleUrl: './diario.scss'
})
export class Diario implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly bienestarService = inject(BienestarService);

  protected readonly cargando = signal(false);
  protected readonly guardando = signal(false);
  protected readonly error = signal('');
  protected readonly mensajeExito = signal('');
  protected readonly entradas = signal<EntradaDiario[]>([]);

  protected readonly mostrarFormulario = signal(false);
  protected readonly editandoId = signal<number | null>(null);
  protected readonly idConfirmandoEliminar = signal<number | null>(null);

  protected readonly opcionesAnimo: OpcionAnimo[] = [
    { valor: 1, etiqueta: 'Muy abrumada', emoji: '🌧️' },
    { valor: 2, etiqueta: 'Desanimada', emoji: '☁️' },
    { valor: 3, etiqueta: 'En proceso / Neutro', emoji: '⛅' },
    { valor: 4, etiqueta: 'Tranquila', emoji: '🌤️' },
    { valor: 5, etiqueta: 'Fuerte y en paz', emoji: '☀️' }
  ];

  protected readonly emocionesSugeridas = [
    'Calma',
    'Esperanza',
    'Ansiedad',
    'Alivio',
    'Tristeza',
    'Valentía',
    'Gratitud'
  ];

  protected readonly formulario = this.fb.nonNullable.group({
    titulo: ['', [Validators.required, Validators.maxLength(150)]],
    estadoAnimo: [3, [Validators.required, Validators.min(1), Validators.max(5)]],
    emocion: ['', [Validators.maxLength(50)]],
    contenido: ['', [Validators.required, Validators.maxLength(2000)]]
  });

  ngOnInit(): void {
    this.cargarEntradas();
  }

  cargarEntradas(): void {
    this.cargando.set(true);
    this.error.set('');

    this.bienestarService
      .listarDiario()
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: datos => this.entradas.set(datos),
        error: err => this.error.set(mensajeDeError(err, 'No se pudieron cargar tus entradas.'))
      });
  }

  nuevaEntrada(): void {
    this.editandoId.set(null);
    this.formulario.reset({
      titulo: '',
      estadoAnimo: 3,
      emocion: '',
      contenido: ''
    });
    this.mensajeExito.set('');
    this.error.set('');
    this.mostrarFormulario.set(true);
  }

  editarEntrada(entrada: EntradaDiario): void {
    this.editandoId.set(entrada.id);
    this.formulario.setValue({
      titulo: entrada.titulo,
      estadoAnimo: entrada.estadoAnimo,
      emocion: entrada.emocion || '',
      contenido: entrada.contenido
    });
    this.mensajeExito.set('');
    this.error.set('');
    this.mostrarFormulario.set(true);
  }

  cancelarFormulario(): void {
    this.mostrarFormulario.set(false);
    this.editandoId.set(null);
    this.formulario.reset();
  }

  seleccionarEmocion(emocion: string): void {
    this.formulario.patchValue({ emocion });
  }

  seleccionarAnimo(valor: number): void {
    this.formulario.patchValue({ estadoAnimo: valor });
  }

  guardar(): void {
    this.error.set('');
    this.mensajeExito.set('');

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const { titulo, estadoAnimo, emocion, contenido } = this.formulario.getRawValue();
    const datos = {
      titulo: titulo.trim(),
      estadoAnimo,
      emocion: emocion.trim() || null,
      contenido: contenido.trim()
    };

    this.guardando.set(true);

    const peticion = this.editandoId()
      ? this.bienestarService.actualizarEntrada(this.editandoId()!, datos)
      : this.bienestarService.crearEntrada(datos);

    peticion.pipe(finalize(() => this.guardando.set(false))).subscribe({
      next: () => {
        this.mensajeExito.set(
          this.editandoId() ? 'Entrada actualizada correctamente.' : 'Nueva entrada guardada en tu diario.'
        );
        this.cancelarFormulario();
        this.cargarEntradas();
      },
      error: err => this.error.set(mensajeDeError(err, 'No se pudo guardar la entrada.'))
    });
  }

  solicitarEliminar(id: number): void {
    this.idConfirmandoEliminar.set(id);
  }

  cancelarEliminar(): void {
    this.idConfirmandoEliminar.set(null);
  }

  confirmarEliminar(id: number): void {
    this.cargando.set(true);
    this.error.set('');

    this.bienestarService
      .eliminarEntrada(id)
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: () => {
          this.idConfirmandoEliminar.set(null);
          this.mensajeExito.set('Entrada eliminada de tu diario.');
          this.cargarEntradas();
        },
        error: err => this.error.set(mensajeDeError(err, 'No se pudo eliminar la entrada.'))
      });
  }

  obtenerEtiquetaAnimo(valor: number): string {
    const encontrada = this.opcionesAnimo.find(o => o.valor === valor);
    return encontrada ? `${encontrada.emoji} ${encontrada.etiqueta}` : `${valor}/5`;
  }
}
