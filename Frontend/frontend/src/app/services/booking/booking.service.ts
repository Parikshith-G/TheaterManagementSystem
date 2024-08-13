import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Booking } from '../../models/Booking';
import { API_BASE_URL_2, API_GATEWAY_URL } from '../../app.constants';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root',
})
export class BookingService {
  url = `${API_GATEWAY_URL}/bookings`;

  constructor(private authService: AuthService, private http: HttpClient) {}

  tkn = this.authService.getToken();

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Bearer ' + this.tkn,
    }),
  };

  createBooking(booking: Booking): Observable<any> {
    return this.http.post(this.url + '/book', booking, {
      headers: this.httpOptions.headers,
      responseType: 'blob',
    });
  }
}
