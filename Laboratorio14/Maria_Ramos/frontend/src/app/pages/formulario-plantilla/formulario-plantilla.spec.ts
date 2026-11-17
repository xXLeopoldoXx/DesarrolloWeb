import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormularioPlantilla } from './formulario-plantilla';

describe('FormularioPlantilla', () => {
  let component: FormularioPlantilla;
  let fixture: ComponentFixture<FormularioPlantilla>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormularioPlantilla],
    }).compileComponents();

    fixture = TestBed.createComponent(FormularioPlantilla);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
