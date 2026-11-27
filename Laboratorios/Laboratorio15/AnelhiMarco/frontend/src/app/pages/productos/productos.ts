import { CurrencyPipe } from '@angular/common';
import { Component, computed, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { catchError, EMPTY, startWith, Subject, switchMap, tap } from 'rxjs';
import { mensajeApi } from '../../core/api';
import { AuthService } from '../../core/auth.service';
import { Producto, ProductoService } from '../../core/producto.service';

@Component({
  imports: [CurrencyPipe, ReactiveFormsModule, RouterLink],
  templateUrl: './productos.html',
})
export class Productos {
  private readonly api = inject(ProductoService);
  private readonly destroy = inject(DestroyRef);
  private readonly consultas = new Subject<{ min: number; max: number } | null>();
  private ultimoFiltro: { min: number; max: number } | null = null;
  protected readonly esAdmin = inject(AuthService).esAdmin;
  private readonly parametros = toSignal(inject(ActivatedRoute).queryParamMap);
  protected readonly accesoRestringido = computed(() => this.parametros()?.get('acceso') === 'admin');
  protected readonly estado = inject(Router).currentNavigation()?.extras.state;
  protected readonly productos = signal<Producto[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal('');
  protected readonly errorFiltro = signal('');
  protected readonly filtro = inject(FormBuilder).group({
    min: [null as number | null],
    max: [null as number | null],
  });

  constructor() {
    this.consultas.pipe(
      startWith(null),
      tap(() => {
        this.cargando.set(true);
        this.error.set('');
      }),
      switchMap((rango) => (rango ? this.api.porPrecio(rango.min, rango.max) : this.api.listar()).pipe(
        catchError((error: unknown) => {
          this.error.set(mensajeApi(error, 'No se pudo cargar el catálogo. Vuelve a intentarlo.'));
          this.cargando.set(false);
          return EMPTY;
        }),
      )),
      takeUntilDestroyed(this.destroy),
    ).subscribe((productos) => {
      this.productos.set(productos);
      this.cargando.set(false);
    });
  }

  protected filtrar(): void {
    const { min, max } = this.filtro.getRawValue();
    if (min === null || max === null || !Number.isFinite(min) || !Number.isFinite(max) || min < 0 || max < min) {
      this.errorFiltro.set('Completa ambos precios. El mínimo debe ser 0 o mayor y el máximo debe ser igual o superior al mínimo.');
      return;
    }
    this.errorFiltro.set('');
    this.ultimoFiltro = { min, max };
    this.reintentar();
  }

  protected limpiar(): void {
    this.filtro.reset();
    this.errorFiltro.set('');
    this.ultimoFiltro = null;
    this.reintentar();
  }

  protected reintentar(): void {
    this.consultas.next(this.ultimoFiltro);
  }
}
