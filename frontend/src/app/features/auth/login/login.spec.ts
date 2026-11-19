import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { Login } from './login';

describe('Login', () => {
  let fixture: ComponentFixture<Login>;
  let html: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [provideRouter([]), provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    await fixture.whenStable();
    html = fixture.nativeElement as HTMLElement;
  });

  it('muestra el botón de salida rápida incluso sin sesión', () => {
    expect(html.querySelector('app-salida-rapida')).toBeTruthy();
  });

  it('al enviar vacío muestra los errores y no llama al servidor', async () => {
    (html.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));
    await fixture.whenStable();

    expect(html.textContent).toContain('Escribe tu usuario.');
    expect(html.textContent).toContain('Escribe tu contraseña.');
  });
});
