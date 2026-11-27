import { HttpErrorResponse } from '@angular/common/http';
import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs';
import { mensajeApi } from '../../core/api';
import { AuthService } from '../../core/auth.service';

@Component({
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly destroy = inject(DestroyRef);
  protected readonly buscando = signal(false);
  protected readonly error = signal('');
  protected readonly sesionVencida = inject(ActivatedRoute).snapshot.queryParamMap.get('estado') === 'sesion-vencida';
  protected readonly formulario = inject(FormBuilder).nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  protected entrar(): void {
    if (this.buscando()) return;
    this.error.set('');
    const { username, password } = this.formulario.getRawValue();
    if (this.formulario.invalid || !username.trim()) {
      this.error.set('Ingresa el usuario y la contraseña.');
      return;
    }

    this.buscando.set(true);
    this.auth.iniciarSesion({ username: username.trim(), password }).pipe(
      takeUntilDestroyed(this.destroy),
      finalize(() => this.buscando.set(false)),
    ).subscribe({
      next: () => void this.router.navigate(['/productos']),
      error: (error: unknown) => this.error.set(
        error instanceof HttpErrorResponse && error.status === 401
          ? 'Usuario o contraseña incorrectos.'
          : mensajeApi(error, 'No pudimos iniciar sesión. Vuelve a intentarlo.'),
      ),
    });
  }
}
