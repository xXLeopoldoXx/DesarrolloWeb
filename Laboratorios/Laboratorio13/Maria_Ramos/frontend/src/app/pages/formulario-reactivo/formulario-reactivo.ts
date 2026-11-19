import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators, FormArray } from '@angular/forms';
import { JsonPipe } from '@angular/common';

import {
  edadMinimaValidator,
  passwordsIgualesValidator
} from '../../validators/registro.validators';

@Component({
  selector: 'app-formulario-reactivo',
  standalone: true,
  imports: [ReactiveFormsModule, JsonPipe],
  templateUrl: './formulario-reactivo.html',
  styleUrl: './formulario-reactivo.scss'
})
export class FormularioReactivo {

  private fb = inject(FormBuilder);

  enviado = false;
  mensajeExito = '';

  formulario = this.fb.group({
    nombre: ['', [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(60)
    ]],

    correo: ['', [
      Validators.required,
      Validators.email
    ]],

    edad: ['', [
      Validators.required,
      edadMinimaValidator(18)
    ]],

    telefono: ['', [
      Validators.pattern(/^\d{9}$/)
    ]],

    password: ['', [
      Validators.required,
      Validators.minLength(8),
      Validators.pattern(/(?=.*[A-Z])(?=.*\d).+/)
    ]],

    confirmarPassword: ['', Validators.required],

    aceptaTerminos: [false, Validators.requiredTrue],

    habilidades: this.fb.array([
      this.fb.control('', [
        Validators.required,
        Validators.minLength(2)
      ])
    ])
  }, {
    validators: passwordsIgualesValidator
  });

  get habilidades(): FormArray {
    return this.formulario.get('habilidades') as FormArray;
  }

  agregarHabilidad(): void {
    this.habilidades.push(
      this.fb.control('', [
        Validators.required,
        Validators.minLength(2)
      ])
    );
  }

  eliminarHabilidad(index: number): void {
    if (this.habilidades.length > 1) {
      this.habilidades.removeAt(index);
    }
  }

  enviarFormulario(): void {
    this.enviado = true;

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    console.log('Registro enviado:', this.formulario.getRawValue());

    this.mensajeExito = '¡Registro completado correctamente!';
  }

  limpiarFormulario(): void {
    this.formulario.reset({
      nombre: '',
      correo: '',
      edad: '',
      telefono: '',
      password: '',
      confirmarPassword: '',
      aceptaTerminos: false
    });

    this.habilidades.clear();

    this.habilidades.push(
      this.fb.control('', [
        Validators.required,
        Validators.minLength(2)
      ])
    );

    this.enviado = false;
    this.mensajeExito = '';
  }
}
