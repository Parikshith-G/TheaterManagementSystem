import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL, API_GATEWAY_URL } from '../../app.constants';
import { Theater } from '../../models/Theater';
import { Room } from '../../models/Room';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root',
})
export class TheaterService {
  private baseUrl = `${API_GATEWAY_URL}/theaters`;

  constructor(private http: HttpClient, private authService: AuthService) {}
  tkn = this.authService.getToken();

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Bearer ' + this.tkn,
    }),
  };

  getTheaters(): Observable<Theater[]> {
    return this.http.get<Theater[]>(`${this.baseUrl}`, this.httpOptions);
  }

  getTheaterById(id: number): Observable<Theater> {
    return this.http.get<Theater>(`${this.baseUrl}/${id}`, this.httpOptions);
  }

  createTheater(theater: Theater): Observable<Theater> {
    return this.http.post<Theater>(this.baseUrl, theater, this.httpOptions);
  }

  updateTheater(theater: Theater): Observable<Theater> {
    return this.http.put<Theater>(
      `${this.baseUrl}/${theater.id}`,
      theater,
      this.httpOptions
    );
  }

  deleteTheater(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, this.httpOptions);
  }

  addRoom(theaterId: number, room: Room): Observable<Room> {
    return this.http.post<Room>(
      `${this.baseUrl}/${theaterId}/rooms`,
      room,
      this.httpOptions
    );
  }

  deleteRoom(theaterId: number, roomId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/${theaterId}/rooms/${roomId}`,
      this.httpOptions
    );
  }
}
