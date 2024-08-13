import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { response } from 'express';
import { Observable } from 'rxjs';
import { API_GATEWAY_URL } from '../../app.constants';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root',
})
export class OtpService {
  private baseUrl = `${API_GATEWAY_URL}/otp`;
  constructor(private http: HttpClient, private authService: AuthService) {}
  generateOtp(email: string): Observable<string> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + this.authService.getToken(),
      }),
      responseType: 'text' as 'json',
    };

    return this.http.post<string>(
      `${this.baseUrl}/generate`,
      email,
      httpOptions
    );
  }

  validateOtp(email: string, otp: any): Observable<boolean> {
    const data = {
      email: email,
      otp: otp,
    };
    return this.http.post<boolean>(`${this.baseUrl}/validate`, data);
  }

  sendDeleteOtpSignal(email: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/delete/${email}`);
  }
}
