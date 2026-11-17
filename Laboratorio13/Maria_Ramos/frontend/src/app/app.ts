import { Component } from '@angular/core';
import {
  RouterLink,
  RouterLinkActive,
  RouterOutlet
} from '@angular/router';

import { AppFooter } from './shared/app-footer/app-footer';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
    RouterOutlet,
    AppFooter
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
}


