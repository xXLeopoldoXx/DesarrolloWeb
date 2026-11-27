import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  imports: [RouterLink],
  template: '<section class="tarjeta formulario"><h1>Página no encontrada</h1><p>Revisa la dirección o vuelve al catálogo.</p><a class="boton" routerLink="/">Volver al catálogo</a></section>',
})
export class NoEncontrado {}
