import { Injectable } from '@angular/core';
import { API_BASE_URL, API_GATEWAY_URL } from '../../app.constants';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Room } from '../../models/Room';
import { Observable } from 'rxjs/internal/Observable';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root',
})
export class RoomService {
  baseUrl = `${API_GATEWAY_URL}/rooms`;
  constructor(private http: HttpClient, private authService: AuthService) {}

  tkn = this.authService.getToken();

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Bearer ' + this.tkn,
    }),
  };

  getRooms(): Observable<Room[]> {
    return this.http.get<Room[]>(`${this.baseUrl}`, this.httpOptions);
  }
  deleteRoomById(id: number): Observable<string> {
    return this.http.delete<string>(`${this.baseUrl}/${id}`, this.httpOptions);
  }

  getRoomById(id: number) {
    return this.http.get<Room>(`${this.baseUrl}/${id}`, this.httpOptions);
  }

  getAvailableSeats(id: number) {
    return this.http.get<number>(
      `${this.baseUrl}/room/${id}`,
      this.httpOptions
    );
  }

  addRoomToTheater(room: Room): Observable<string> {
    return this.http.post<string>(`${this.baseUrl}`, room, this.httpOptions);
  }
}
