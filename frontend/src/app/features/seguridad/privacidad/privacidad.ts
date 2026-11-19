import { HttpErrorResponse } from '@angular/common/http';
import { afterNextRender, Component, DestroyRef, ElementRef, inject, Injector, OnInit, signal, viewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { AuthService } from '../../../core/auth/auth.service';
import { mensajeDeError } from '../../../core/http-errors';
import { SeguridadService } from '../seguridad.service';

@Component({
  selector: 'app-privacidad-seguridad',
  imports: [RouterLink],
  templateUrl: './privacidad.html',
  styleUrl: '../seguridad.scss',
})
export class Privacidad implements OnInit {
  private readonly seguridad = inject(SeguridadService);
  private readonly auth = inject(AuthService);
  private readonly destruir = inject(DestroyRef);
  private readonly injector = inject(Injector);
  private readonly botonEliminar = viewChild<ElementRef<HTMLButtonElement>>('botonEliminar');
  private readonly botonCancelar = viewChild<ElementRef<HTMLButtonElement>>('botonCancelar');

  protected readonly cargando = signal(true);
  protected readonly disponible = signal(false);
  protected readonly confirmando = signal(false);
  protected readonly eliminando = signal(false);
  protected readonly errorEstado = signal('');
  protected readonly errorEliminar = signal('');

  ngOnInit(): void {
    this.consultarDisponibilidad();
  }

  protected consultarDisponibilidad(): void {
    this.cargando.set(true);
    this.errorEstado.set('');
    this.seguridad.estadoBorradoCuenta().pipe(
      takeUntilDestroyed(this.destruir),
      finalize(() => this.cargando.set(false)),
    ).subscribe({
      next: estado => this.disponible.set(estado.disponible),
      error: error => this.errorEstado.set(mensajeDeError(error, 'No se pudo verificar la disponibilidad.')),
    });
  }

  protected solicitarConfirmacion(): void {
    if (!this.disponible()) return;

    this.confirmando.set(true);
    afterNextRender(() => this.botonCancelar()?.nativeElement.focus(), { injector: this.injector });
  }

  protected cancelar(): void {
    this.confirmando.set(false);
    this.errorEliminar.set('');
    afterNextRender(() => this.botonEliminar()?.nativeElement.focus(), { injector: this.injector });
  }

  protected confirmarEliminacion(): void {
    if (!this.disponible() || !this.confirmando() || this.eliminando()) return;

    this.eliminando.set(true);
    this.errorEliminar.set('');
    this.seguridad.eliminarCuenta().pipe(
      takeUntilDestroyed(this.destruir),
      finalize(() => this.eliminando.set(false)),
    ).subscribe({
      next: () => this.auth.cerrarSesion(),
      error: error => {
        if (error instanceof HttpErrorResponse && error.status === 503) {
          this.disponible.set(false);
          this.confirmando.set(false);
        } else {
          this.errorEliminar.set(mensajeDeError(error, 'No se pudo confirmar la eliminación de tu cuenta.'));
        }
      },
    });
  }
}
