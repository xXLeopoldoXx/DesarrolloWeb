import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { JsonPipe } from '@angular/common';

import {
  Contacto,
  crearContactoVacio
} from '../../models/contacto';

@Component({
  selector: 'app-formulario-plantilla',
  standalone: true,
  imports: [FormsModule, JsonPipe],
  templateUrl: './formulario-plantilla.html',
  styleUrl: './formulario-plantilla.scss'
})
export class FormularioPlantilla {

  contacto: Contacto = crearContactoVacio();

  enviado = false;
  mensajeExito = '';

  enviarFormulario(formulario: NgForm): void {
    this.enviado = true;

    if (formulario.invalid) {
      formulario.control.markAllAsTouched();
      return;
    }

    console.log('Formulario enviado:', this.contacto);

    this.mensajeExito = '¡Tu mensaje se envió correctamente!';
  }

  limpiarFormulario(formulario: NgForm): void {
    formulario.resetForm();
    this.contacto = crearContactoVacio();
    this.enviado = false;
    this.mensajeExito = '';
  }
}
