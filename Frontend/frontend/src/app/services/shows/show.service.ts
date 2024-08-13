import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_BASE_URL, API_GATEWAY_URL } from '../../app.constants';
import { Show } from '../../models/Show';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root',
})
export class ShowService {
  apiUrl = `${API_GATEWAY_URL}/shows`;
  constructor(private http: HttpClient, private authService: AuthService) {}

  tkn = this.authService.getToken();

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Bearer ' + this.tkn,
    }),
  };

  getShowById(id: number): Observable<Show> {
    return this.http.get<Show>(`${this.apiUrl}/${id}`, this.httpOptions);
  }

  getShows(): Observable<Show[]> {
    return this.http.get<Show[]>(this.apiUrl, this.httpOptions);
  }

  createShow(show: Show): Observable<Show> {
    return this.http.post<Show>(this.apiUrl, show, this.httpOptions);
  }

  deleteShow(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, this.httpOptions);
  }
}
