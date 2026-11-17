import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { Categorias } from './categorias';

describe('Categorias', () => {
  let component: Categorias;
  let fixture: ComponentFixture<Categorias>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Categorias],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(Categorias);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
