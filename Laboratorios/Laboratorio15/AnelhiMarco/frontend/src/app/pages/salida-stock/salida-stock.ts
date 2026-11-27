import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize, Subscription } from 'rxjs';
import { mensajeApi } from '../../core/api';
import { Producto, ProductoService } from '../../core/producto.service';

@Component({
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './salida-stock.html',
  styleUrl: './salida-stock.scss',
})
export class SalidaStock {
  private readonly api = inject(ProductoService);
  private readonly router = inject(Router);
  private readonly destroy = inject(DestroyRef);
  private id = 0;
  private lectura?: Subscription;
  protected readonly producto = signal<Producto | null>(null);
  protected readonly cargando = signal(false);
  protected readonly enviando = signal(false);
  protected readonly recargable = signal(false);
  protected readonly error = signal('');
  protected readonly errorCantidad = signal('');
  protected readonly formulario = inject(FormBuilder).group({
    cantidad: [1 as number | null, [Validators.required, Validators.min(1)]],
  });

  constructor() {
    this.formulario.controls.cantidad.valueChanges.pipe(takeUntilDestroyed(this.destroy))
      .subscribe(() => this.errorCantidad.set(''));
    inject(ActivatedRoute).paramMap.pipe(takeUntilDestroyed(this.destroy)).subscribe((params) => {
      this.id = Number(params.get('id'));
      this.producto.set(null);
      this.formulario.reset({ cantidad: 1 });
      this.cargar();
    });
  }

  protected cargar(): void {
    this.lectura?.unsubscribe();
    this.error.set('');
    this.errorCantidad.set('');
    if (!Number.isSafeInteger(this.id) || this.id < 1) {
      this.recargable.set(false);
      this.error.set('El identificador del producto debe ser un entero positivo.');
      return;
    }

    this.recargable.set(true);
    this.cargando.set(true);
    this.lectura = this.api.porId(this.id).pipe(
      takeUntilDestroyed(this.destroy),
      finalize(() => this.cargando.set(false)),
    ).subscribe({
      next: (producto) => this.producto.set(producto),
      error: (error: unknown) => this.error.set(mensajeApi(error, 'No se pudo cargar el producto.')),
    });
  }

  protected guardar(): void {
    const producto = this.producto();
    const { cantidad } = this.formulario.getRawValue();
    if (!producto || this.enviando() || this.cargando()) return;
    this.error.set('');
    this.errorCantidad.set('');
    if (cantidad === null || !Number.isSafeInteger(cantidad) || cantidad < 1) {
      this.errorCantidad.set('Ingresa una cantidad entera mayor o igual a 1.');
      return;
    }
    if (cantidad > producto.stock) {
      this.errorCantidad.set('La cantidad supera el stock disponible.');
      return;
    }

    this.enviando.set(true);
    this.api.registrarSalida(producto.id, cantidad).pipe(
      takeUntilDestroyed(this.destroy),
      finalize(() => this.enviando.set(false)),
    ).subscribe({
      next: (actualizado) => void this.router.navigate(['/productos'], {
        state: { salida: true, stock: actualizado.stock },
      }),
      error: (error: unknown) => this.error.set(mensajeApi(error,
        'No se pudo confirmar la salida. Consulta el stock antes de volver a enviarla.')),
    });
  }
}
