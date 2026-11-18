import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { AuthService } from '../../../core/auth/auth.service';
import { mensajeDeError } from '../../../core/http-errors';
import { AuthLayout } from '../auth-layout/auth-layout';
import { clavesIgualesValidator, PATRON_CLAVE, PATRON_USUARIO } from './registro.validators';

@Component({
  selector: 'app-registro',
  imports: [ReactiveFormsModule, RouterLink, AuthLayout],
  templateUrl: './registro.html',
  styleUrl: './registro.scss'
})
export class Registro {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly cargando = signal(false);
  protected readonly error = signal('');

  protected readonly formulario = this.fb.nonNullable.group(
    {
      seudonimo: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(40)]],
      username: ['', [Validators.required, Validators.pattern(PATRON_USUARIO)]],
      password: [
        '',
        [Validators.required, Validators.minLength(8), Validators.maxLength(72), Validators.pattern(PATRON_CLAVE)]
      ],
      confirmarPassword: ['', Validators.required],
      aceptaPrivacidad: [false, Validators.requiredTrue]
    },
    { validators: clavesIgualesValidator }
  );

  protected registrar(): void {
    this.error.set('');

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const { seudonimo, username, password } = this.formulario.getRawValue();
    this.cargando.set(true);

    this.auth
      .registro({ seudonimo, username, password })
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: () => this.router.navigate(['/inicio']),
        error: error => this.error.set(mensajeDeError(error, 'No se pudo crear la cuenta.'))
      });
  }
}
