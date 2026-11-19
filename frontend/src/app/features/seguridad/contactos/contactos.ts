import { Component, DestroyRef, ElementRef, inject, OnInit, signal, viewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { mensajeDeError } from '../../../core/http-errors';
import { Contacto } from '../seguridad.models';
import { SeguridadService } from '../seguridad.service';
import { noBlanco, PATRON_TELEFONO } from '../validacion-seguridad';

@Component({
  selector: 'app-contactos-confianza',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './contactos.html',
  styleUrl: '../seguridad.scss',
})
export class Contactos implements OnInit {
  private readonly seguridad = inject(SeguridadService);
  private readonly destruir = inject(DestroyRef);
  private readonly fb = inject(FormBuilder).nonNullable;
  private readonly campoNombre = viewChild.required<ElementRef<HTMLInputElement>>('campoNombre');

  protected readonly contactos = signal<Contacto[]>([]);
  protected readonly cargando = signal(true);
  protected readonly ocupada = signal(false);
  protected readonly errorCarga = signal('');
  protected readonly errorOperacion = signal('');
  protected readonly editando = signal<number | null>(null);

  protected readonly formulario = this.fb.group({
    nombre: ['', [noBlanco, Validators.maxLength(100)]],
    telefono: ['', [Validators.required, Validators.pattern(PATRON_TELEFONO)]],
    relacion: ['', Validators.maxLength(100)],
  });

  ngOnInit(): void {
    this.cargar();
  }

  protected cargar(): void {
    this.cargando.set(true);
    this.errorCarga.set('');
    this.seguridad.listarContactos().pipe(
      takeUntilDestroyed(this.destruir),
      finalize(() => this.cargando.set(false)),
    ).subscribe({
      next: contactos => this.contactos.set(contactos),
      error: error => this.errorCarga.set(mensajeDeError(error, 'No se pudo cargar la red de confianza.')),
    });
  }

  protected editar(contacto: Contacto): void {
    this.editando.set(contacto.id);
    this.errorOperacion.set('');
    this.formulario.reset({
      nombre: contacto.nombre,
      telefono: contacto.telefono,
      relacion: contacto.relacion ?? '',
    });
    this.campoNombre().nativeElement.focus();
  }

  protected cancelarEdicion(): void {
    this.restablecerFormulario();
    this.errorOperacion.set('');
    this.campoNombre().nativeElement.focus();
  }

  protected guardar(): void {
    this.formulario.markAllAsTouched();
    if (this.formulario.invalid || this.ocupada() || this.cargando() || this.errorCarga()) return;

    const id = this.editando();
    const datos = this.formulario.getRawValue();
    const solicitud = id === null ? this.seguridad.crearContacto(datos) : this.seguridad.actualizarContacto(id, datos);
    this.ocupada.set(true);
    this.errorOperacion.set('');
    solicitud.pipe(
      takeUntilDestroyed(this.destruir),
      finalize(() => this.ocupada.set(false)),
    ).subscribe({
      next: contacto => {
        this.contactos.update(lista => id === null
          ? [...lista, contacto]
          : lista.map(item => item.id === id ? contacto : item));
        this.restablecerFormulario();
      },
      error: error => this.errorOperacion.set(mensajeDeError(error)),
    });
  }

  protected eliminar(id: number): void {
    if (this.ocupada()) return;

    this.ocupada.set(true);
    this.errorOperacion.set('');
    this.seguridad.eliminarContacto(id).pipe(
      takeUntilDestroyed(this.destruir),
      finalize(() => this.ocupada.set(false)),
    ).subscribe({
      next: () => {
        this.contactos.update(lista => lista.filter(item => item.id !== id));
        if (this.editando() === id) this.restablecerFormulario();
      },
      error: error => this.errorOperacion.set(mensajeDeError(error)),
    });
  }

  private restablecerFormulario(): void {
    this.editando.set(null);
    this.formulario.reset();
  }
}
