import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GoldToastrComponent } from './gold-toastr.component';

describe('GoldToastrComponent', () => {
  let component: GoldToastrComponent;
  let fixture: ComponentFixture<GoldToastrComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GoldToastrComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GoldToastrComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
