import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { AuthService } from '../../../core/auth/auth.service';
import { mensajeDeError } from '../../../core/http-errors';
import { AuthLayout } from '../auth-layout/auth-layout';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink, AuthLayout],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly cargando = signal(false);
  protected readonly error = signal('');

  protected readonly formulario = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  protected entrar(): void {
    this.error.set('');

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const { username, password } = this.formulario.getRawValue();
    this.cargando.set(true);

    this.auth
      .login(username, password)
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: () => this.router.navigate(['/inicio']),
        error: error => this.error.set(mensajeDeError(error, 'No se pudo iniciar sesión.'))
      });
  }
}
