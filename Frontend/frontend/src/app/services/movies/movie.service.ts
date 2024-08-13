import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Movie } from '../../models/Movie';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';
import { API_GATEWAY_URL } from '../../app.constants';

@Injectable({
  providedIn: 'root',
})
export class MovieService {
  private apiUrl = `${API_GATEWAY_URL}/movies`;
  constructor(private http: HttpClient, private authService: AuthService) {}

  tkn = this.authService.getToken();

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Bearer ' + this.tkn,
    }),
  };

  getMovies(): Observable<Movie[]> {
    return this.http.get<Movie[]>(this.apiUrl, this.httpOptions);
  }

  createMovie(movie: Movie): Observable<Movie> {
    return this.http.post<Movie>(this.apiUrl, movie, this.httpOptions);
  }

  deleteMovie(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, this.httpOptions);
  }

  getMovieById(id: number): Observable<Movie> {
    return this.http.get<Movie>(`${this.apiUrl}/${id}`, this.httpOptions);
  }
}
