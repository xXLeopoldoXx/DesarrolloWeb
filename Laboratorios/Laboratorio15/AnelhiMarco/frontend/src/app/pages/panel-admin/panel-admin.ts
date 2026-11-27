import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
import { mensajeApi } from '../../core/api';
import { ProductoService, ResumenAdmin } from '../../core/producto.service';

@Component({ templateUrl: './panel-admin.html' })
export class PanelAdmin {
  private readonly api = inject(ProductoService);
  private readonly destroy = inject(DestroyRef);
  protected readonly resumen = signal<ResumenAdmin | null>(null);
  protected readonly cargando = signal(false);
  protected readonly error = signal('');

  constructor() {
    this.cargar();
  }

  protected cargar(): void {
    if (this.cargando()) return;
    this.cargando.set(true);
    this.error.set('');
    this.api.resumen().pipe(
      takeUntilDestroyed(this.destroy),
      finalize(() => this.cargando.set(false)),
    ).subscribe({
      next: (resumen) => this.resumen.set(resumen),
      error: (error: unknown) => this.error.set(mensajeApi(error, 'No se pudo cargar el resumen.')),
    });
  }
}
