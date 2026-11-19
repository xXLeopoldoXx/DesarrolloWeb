import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { ProductoCard } from './producto-card';

describe('ProductoCard', () => {
  let component: ProductoCard;
  let fixture: ComponentFixture<ProductoCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductoCard],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(ProductoCard);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('producto', {
      id: 1,
      nombre: 'Laptop Pro',
      categoria: 'Tecnología',
      precio: 3699,
      descripcion: 'Equipo para desarrollo y productividad.',
    });
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
