import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllUsersNoAdminsComponent } from './all-users-no-admins.component';

describe('AllUsersNoAdminsComponent', () => {
  let component: AllUsersNoAdminsComponent;
  let fixture: ComponentFixture<AllUsersNoAdminsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllUsersNoAdminsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AllUsersNoAdminsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
