import { inject, Injectable } from '@angular/core';
import { Booking } from '../../models/Booking';
import { WildCardRedirectsComponent } from '../../components/wild-card-redirects/wild-card-redirects.component';

@Injectable({
  providedIn: 'root',
})
export class PaymentService {
  static fromBrowser: boolean = false;
  booking: Booking;
  constructor() {
    this.booking = new Booking(
      0,
      '',
      0,
      new Date(),
      0,
      0,
      '',
      new Date(),
      new Date(),
      new Date(),
      ''
    );
  }
  toggleFromBrowser() {
    PaymentService.fromBrowser = !PaymentService.fromBrowser;
  }
  getState() {
    PaymentService.fromBrowser;
  }

  getBooking() {
    return this.booking;
  }

  setBooking(booking: Booking) {
    this.booking = booking;
  }
}
