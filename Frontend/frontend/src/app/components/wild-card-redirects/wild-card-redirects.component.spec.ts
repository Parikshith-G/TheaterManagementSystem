import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WildCardRedirectsComponent } from './wild-card-redirects.component';

describe('WildCardRedirectsComponent', () => {
  let component: WildCardRedirectsComponent;
  let fixture: ComponentFixture<WildCardRedirectsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WildCardRedirectsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(WildCardRedirectsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
