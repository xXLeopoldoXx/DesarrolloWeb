import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Icono } from '../../shared/icono/icono';

@Component({
  selector: 'app-seguridad',
  imports: [RouterLink, Icono],
  templateUrl: './seguridad.html',
  styleUrl: './seguridad.scss',
})
export class Seguridad {}
