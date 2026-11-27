import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { mensajeApi } from '../../core/api';
import { ProductoService } from '../../core/producto.service';

@Component({
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './nuevo-producto.html',
})
export class NuevoProducto {
  private readonly api = inject(ProductoService);
  private readonly router = inject(Router);
  private readonly destroy = inject(DestroyRef);
  protected readonly guardando = signal(false);
  protected readonly error = signal('');
  protected readonly errorCampo = signal('');
  protected readonly formulario = inject(FormBuilder).group({
    nombre: ['', Validators.required],
    categoria: ['', Validators.required],
    precio: [null as number | null, Validators.required],
    stock: [null as number | null, Validators.required],
  });

  protected guardar(): void {
    if (this.guardando()) return;
    this.error.set('');
    this.errorCampo.set('');
    const { nombre, categoria, precio, stock } = this.formulario.getRawValue();
    if (!nombre?.trim()) {
      this.errorCampo.set('Ingresa el nombre del producto.');
      return;
    }
    if (!categoria?.trim()) {
      this.errorCampo.set('Ingresa la categoría.');
      return;
    }
    if (precio === null || !Number.isFinite(precio) || precio <= 0) {
      this.errorCampo.set('Ingresa un precio mayor que cero.');
      return;
    }
    if (stock === null || !Number.isInteger(stock) || stock < 0 || stock > 2_147_483_647) {
      this.errorCampo.set('Ingresa un stock entero entre 0 y 2147483647.');
      return;
    }

    this.guardando.set(true);
    this.api.crear({ nombre: nombre.trim(), categoria: categoria.trim(), precio, stock }).pipe(
      takeUntilDestroyed(this.destroy),
      finalize(() => this.guardando.set(false)),
    ).subscribe({
      next: () => void this.router.navigate(['/productos'], { state: { alta: true } }),
      error: (error: unknown) => this.error.set(mensajeApi(error, 'No se pudo crear el producto.')),
    });
  }
}
