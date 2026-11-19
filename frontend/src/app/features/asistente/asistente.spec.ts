import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { API_URL } from '../../core/config';
import { Asistente } from './asistente';

describe('Asistente (chat)', () => {
  let fixture: ComponentFixture<Asistente>;
  let http: HttpTestingController;
  let html: HTMLElement;

  async function crear(disponible = true): Promise<void> {
    await TestBed.configureTestingModule({
      imports: [Asistente],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    http = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(Asistente);
    await fixture.whenStable();
    http.expectOne(`${API_URL}/asistente/estado`).flush({ disponible });
    await fixture.whenStable();
    html = fixture.nativeElement as HTMLElement;
  }

  async function escribirYEnviar(texto: string): Promise<void> {
    const cuadro = html.querySelector('textarea') as HTMLTextAreaElement;
    cuadro.value = texto;
    cuadro.dispatchEvent(new Event('input'));
    await fixture.whenStable();
    html.querySelector('form')!.dispatchEvent(new Event('submit', { cancelable: true }));
    await fixture.whenStable();
  }

  const burbujas = () => html.querySelectorAll('.burbuja:not(.burbuja--escribiendo)');

  afterEach(() => http.verify());

  it('muestra la bienvenida y las sugerencias al empezar', async () => {
    await crear();

    expect(html.textContent).toContain('Puedes empezar con');
    expect(html.querySelectorAll('.chat__sugerencia').length).toBe(3);
    expect(burbujas().length).toBe(0);
  });

  it('envía el mensaje y muestra la respuesta del asistente', async () => {
    await crear();

    await escribirYEnviar('  ¿Qué es un plan de seguridad? ');

    const peticion = http.expectOne(`${API_URL}/asistente/mensajes`);
    expect(peticion.request.body).toEqual({ mensaje: '¿Qué es un plan de seguridad?', historial: [] });
    peticion.flush({ respuesta: 'Es una guía para protegerte.', emergencia: false });
    await fixture.whenStable();

    expect(burbujas().length).toBe(2);
    expect(html.textContent).toContain('¿Qué es un plan de seguridad?');
    expect(html.textContent).toContain('Es una guía para protegerte.');
    expect(html.querySelector('a[href="tel:105"].boton')).toBeNull(); // sin emergencia no hay botones de llamada
  });

  it('el segundo mensaje reenvía la conversación anterior como historial', async () => {
    await crear();
    await escribirYEnviar('hola');
    http.expectOne(`${API_URL}/asistente/mensajes`).flush({ respuesta: 'Buenas', emergencia: false });
    await fixture.whenStable();

    await escribirYEnviar('¿y ahora?');

    const peticion = http.expectOne(`${API_URL}/asistente/mensajes`);
    expect(peticion.request.body.historial).toEqual([
      { rol: 'usuaria', texto: 'hola' },
      { rol: 'asistente', texto: 'Buenas' },
    ]);
    peticion.flush({ respuesta: 'ok', emergencia: false });
  });

  it('si es una emergencia muestra los botones para llamar al 105 y a la Línea 100', async () => {
    await crear();
    await escribirYEnviar('Estoy en peligro');

    http.expectOne(`${API_URL}/asistente/mensajes`).flush({ respuesta: 'Llama al 105', emergencia: true });
    await fixture.whenStable();

    expect(html.querySelector('.burbuja--emergencia')).toBeTruthy();
    expect(html.querySelector('.burbuja__llamar a[href="tel:105"]')).toBeTruthy();
    expect(html.querySelector('.burbuja__llamar a[href="tel:100"]')).toBeTruthy();
  });

  it('muestra el mensaje de error del servidor (503) y permite reintentar', async () => {
    await crear();
    await escribirYEnviar('hola');

    http
      .expectOne(`${API_URL}/asistente/mensajes`)
      .flush({ mensaje: 'El asistente no está disponible en este momento.' }, { status: 503, statusText: 'Unavailable' });
    await fixture.whenStable();

    expect(html.querySelector('[role="alert"]')?.textContent).toContain('El asistente no está disponible');
    expect((html.querySelector('button[type="submit"]') as HTMLButtonElement).disabled).toBe(true); // cuadro vacío
  });

  it('no envía mensajes vacíos', async () => {
    await crear();

    await escribirYEnviar('   ');

    http.expectNone(`${API_URL}/asistente/mensajes`);
    expect(burbujas().length).toBe(0);
  });

  it('si el backend no tiene clave de Gemini, avisa y bloquea el envío', async () => {
    await crear(false);

    expect(html.textContent).toContain('aún no está configurado');
    expect((html.querySelector('button[type="submit"]') as HTMLButtonElement).disabled).toBe(true);
    html.querySelectorAll<HTMLButtonElement>('.chat__sugerencia').forEach(b => expect(b.disabled).toBe(true));
  });

  it('borrar la conversación vacía la pantalla', async () => {
    await crear();
    await escribirYEnviar('hola');
    http.expectOne(`${API_URL}/asistente/mensajes`).flush({ respuesta: 'Buenas', emergencia: false });
    await fixture.whenStable();

    (html.querySelector('.chat__limpiar') as HTMLButtonElement).click();
    await fixture.whenStable();

    expect(burbujas().length).toBe(0);
  });
});
