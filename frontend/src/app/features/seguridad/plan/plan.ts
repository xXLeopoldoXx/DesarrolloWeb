import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize, Observable } from 'rxjs';

import { mensajeDeError } from '../../../core/http-errors';
import { Accion, Lugar } from '../seguridad.models';
import { SeguridadService } from '../seguridad.service';
import { noBlanco } from '../validacion-seguridad';

@Component({
  selector: 'app-plan-seguridad',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './plan.html',
  styleUrl: '../seguridad.scss',
})
export class Plan implements OnInit {
  private readonly seguridad = inject(SeguridadService);
  private readonly destruir = inject(DestroyRef);
  private readonly fb = inject(FormBuilder).nonNullable;

  protected readonly acciones = signal<Accion[]>([]);
  protected readonly lugares = signal<Lugar[]>([]);
  protected readonly cargandoAcciones = signal(true);
  protected readonly cargandoLugares = signal(true);
  protected readonly ocupadasAcciones = signal(false);
  protected readonly ocupadosLugares = signal(false);
  protected readonly errorCargaAcciones = signal('');
  protected readonly errorCargaLugares = signal('');
  protected readonly errorAccion = signal('');
  protected readonly errorLugar = signal('');

  protected readonly accionForm = this.fb.group({
    texto: ['', [noBlanco, Validators.maxLength(240)]],
  });

  protected readonly lugarForm = this.fb.group({
    nombre: ['', [noBlanco, Validators.maxLength(100)]],
    referencia: ['', Validators.maxLength(250)],
    nota: ['', Validators.maxLength(500)],
  });

  ngOnInit(): void {
    this.cargarAcciones();
    this.cargarLugares();
  }

  protected cargarAcciones(): void {
    this.cargandoAcciones.set(true);
    this.errorCargaAcciones.set('');
    this.seguridad.listarAcciones().pipe(
      takeUntilDestroyed(this.destruir),
      finalize(() => this.cargandoAcciones.set(false)),
    ).subscribe({
      next: acciones => this.acciones.set(acciones),
      error: error => this.errorCargaAcciones.set(mensajeDeError(error, 'No se pudieron cargar las acciones.')),
    });
  }

  protected cargarLugares(): void {
    this.cargandoLugares.set(true);
    this.errorCargaLugares.set('');
    this.seguridad.listarLugares().pipe(
      takeUntilDestroyed(this.destruir),
      finalize(() => this.cargandoLugares.set(false)),
    ).subscribe({
      next: lugares => this.lugares.set(lugares),
      error: error => this.errorCargaLugares.set(mensajeDeError(error, 'No se pudieron cargar los lugares.')),
    });
  }

  protected crearAccion(): void {
    this.accionForm.markAllAsTouched();
    if (this.accionForm.invalid || this.cargandoAcciones() || this.errorCargaAcciones()) return;

    this.operarAcciones(this.seguridad.crearAccion(this.accionForm.getRawValue().texto.trim()), accion => {
      this.acciones.update(lista => [...lista, accion]);
      this.accionForm.reset();
    });
  }

  protected marcarAccion(accion: Accion, casilla: HTMLInputElement): void {
    const completada = casilla.checked;
    casilla.checked = accion.completada;
    this.operarAcciones(this.seguridad.marcarAccion(accion.id, completada), actualizada => {
      this.acciones.update(lista => lista.map(item => item.id === actualizada.id ? actualizada : item));
    });
  }

  protected eliminarAccion(id: number): void {
    this.operarAcciones(this.seguridad.eliminarAccion(id), () => {
      this.acciones.update(lista => lista.filter(accion => accion.id !== id));
    });
  }

  protected crearLugar(): void {
    this.lugarForm.markAllAsTouched();
    if (this.lugarForm.invalid || this.cargandoLugares() || this.errorCargaLugares()) return;

    this.operarLugares(this.seguridad.crearLugar(this.lugarForm.getRawValue()), lugar => {
      this.lugares.update(lista => [...lista, lugar]);
      this.lugarForm.reset();
    });
  }

  protected eliminarLugar(id: number): void {
    this.operarLugares(this.seguridad.eliminarLugar(id), () => {
      this.lugares.update(lista => lista.filter(lugar => lugar.id !== id));
    });
  }

  private operarAcciones<T>(solicitud: Observable<T>, aplicar: (respuesta: T) => void): void {
    if (this.ocupadasAcciones()) return;

    this.ocupadasAcciones.set(true);
    this.errorAccion.set('');
    solicitud.pipe(
      takeUntilDestroyed(this.destruir),
      finalize(() => this.ocupadasAcciones.set(false)),
    ).subscribe({
      next: aplicar,
      error: error => this.errorAccion.set(mensajeDeError(error)),
    });
  }

  private operarLugares<T>(solicitud: Observable<T>, aplicar: (respuesta: T) => void): void {
    if (this.ocupadosLugares()) return;

    this.ocupadosLugares.set(true);
    this.errorLugar.set('');
    solicitud.pipe(
      takeUntilDestroyed(this.destruir),
      finalize(() => this.ocupadosLugares.set(false)),
    ).subscribe({
      next: aplicar,
      error: error => this.errorLugar.set(mensajeDeError(error)),
    });
  }
}
