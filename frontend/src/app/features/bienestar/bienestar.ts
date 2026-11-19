import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { Icono } from '../../shared/icono/icono';

@Component({
  selector: 'app-bienestar',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, Icono],
  templateUrl: './bienestar.html',
  styleUrl: './bienestar.scss'
})
export class Bienestar {}
