import { TestBed } from '@angular/core/testing';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
    }).compileComponents();
  });

  it('debe crear la aplicación', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('debe mostrar el título del catálogo', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.querySelector('h1')?.textContent)
      .toContain('Catálogo de productos');
  });

  it('debe mostrar tres productos', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.querySelectorAll('.product-card').length)
      .toBe(3);
  });

  it('debe ocultar y mostrar el stock', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const button = compiled.querySelector('button')!;

    expect(compiled.querySelectorAll('.stock').length)
      .toBe(3);

    button.click();
    fixture.detectChanges();

    expect(compiled.querySelectorAll('.stock').length)
      .toBe(0);

    button.click();
    fixture.detectChanges();

    expect(compiled.querySelectorAll('.stock').length)
      .toBe(3);
  });
});