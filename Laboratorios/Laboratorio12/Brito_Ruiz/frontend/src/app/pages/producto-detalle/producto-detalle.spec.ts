import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { ProductoDetalle } from './producto-detalle';

describe('ProductoDetalle', () => {
  let component: ProductoDetalle;
  let fixture: ComponentFixture<ProductoDetalle>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductoDetalle],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(ProductoDetalle);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
