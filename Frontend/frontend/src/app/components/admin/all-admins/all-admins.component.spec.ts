import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllAdminsComponent } from './all-admins.component';

describe('AllAdminsComponent', () => {
  let component: AllAdminsComponent;
  let fixture: ComponentFixture<AllAdminsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllAdminsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AllAdminsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
