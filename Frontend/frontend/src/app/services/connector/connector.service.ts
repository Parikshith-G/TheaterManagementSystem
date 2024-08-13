import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { Theater } from '../../models/Theater';
import UserLogin from '../../models/user-login';
import { UserRegistration } from '../../models/user-registration';
import { API_GATEWAY_URL } from '../../app.constants';

@Injectable({
  providedIn: 'root',
})
export class ConnectorService {
  private apiUrl = `${API_GATEWAY_URL}/user`;

  constructor(private httpClient: HttpClient) {}
  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Bearer ',
    }),
  };
  doLogin(login: UserLogin): Observable<any> {
    const data = {
      email: login.emailId,
      password: login.password,
    };

    return this.httpClient
      .post<any>(this.apiUrl + '/login', data, this.httpOptions)
      .pipe(catchError(this.errorHandler));
  }

  doSignUp(registerObj: UserRegistration) {
    const data = {
      name: registerObj.username,
      password: registerObj.password,
      email: registerObj.emailId,
      phone: registerObj.phone,
      role: 'USER',
    };

    return this.httpClient
      .post(this.apiUrl + '/register', data, this.httpOptions)
      .pipe(catchError(this.errorHandler));
  }

  errorHandler(error: any) {
    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      // Get client-side error
      errorMessage = error.error.message;
    } else {
      // Get server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }

    return throwError(() => error);
  }
}
