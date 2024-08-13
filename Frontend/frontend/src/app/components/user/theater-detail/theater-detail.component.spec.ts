import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TheaterDetailComponent } from './theater-detail.component';

describe('TheaterDetailComponent', () => {
  let component: TheaterDetailComponent;
  let fixture: ComponentFixture<TheaterDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TheaterDetailComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TheaterDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
