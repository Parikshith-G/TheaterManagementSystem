import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeatStatusDialogComponent } from './seat-status-dialog.component';

describe('SeatStatusDialogComponent', () => {
  let component: SeatStatusDialogComponent;
  let fixture: ComponentFixture<SeatStatusDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SeatStatusDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SeatStatusDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
