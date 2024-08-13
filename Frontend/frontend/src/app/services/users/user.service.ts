import { inject, Injectable } from '@angular/core';
import {
  API_BASE_URL,
  API_BASE_URL_3,
  API_GATEWAY_URL,
} from '../../app.constants';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { UserRegistration } from '../../models/user-registration';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { User } from '../../models/user';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private baseUrl = `${API_GATEWAY_URL}/user`;
  constructor(private http: HttpClient, private authService: AuthService) {}

  getAllUsersExceptAdmins(): Observable<User[]> {
    return this.http.get<User[]>(
      `${this.baseUrl}/admin/all/${this.authService.getToken()}`
    );
  }
  dublicateEmail(email: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/email/${email}`);
  }

  getAllAdmins(): Observable<User[]> {
    return this.http.get<User[]>(
      `${this.baseUrl}/god/admins/${this.authService.getToken()}`
    );
  }

  updateUserToAdmin(id: number): Observable<boolean> {
    return this.http.put<boolean>(
      `${this.baseUrl}/god/promote/${id}/${this.authService.getToken()}`,
      ''
    );
  }

  updateAdminToUser(id: number): Observable<boolean> {
    return this.http.put<boolean>(
      `${this.baseUrl}/god/demote/${id}/${this.authService.getToken()}`,
      ''
    );
  }
  updateUser(user: {
    id: number;
    name: string;
    email: string;
    password: string;
    phone: string;
  }): Observable<string> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + this.authService.getToken(),
      }),
      responseType: 'text' as 'json',
    };

    return this.http.put<string>(
      `${this.baseUrl}/user/edit/${this.authService.getToken()}`,
      user,
      httpOptions
    );
  }

  deleteUser(id: number): Observable<boolean> {
    return this.http.delete<boolean>(
      `${this.baseUrl}/user/${id}/${this.authService.getToken()}`
    );
  }

  overrideDeleteAdmin(id: number): Observable<boolean> {
    const path = this.authService.getUserRole() === 'admin' ? 'admin' : 'god';

    return this.http.delete<boolean>(
      `${this.baseUrl}/${path}/${id}/${this.authService.getToken()}`
    );
  }

  overrideDeleteGod(id: number): Observable<boolean> {
    return this.http.delete<boolean>(
      `${this.baseUrl}/god/${id}/${this.authService.getToken()}`
    );
  }

  doesEmailExist(email: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/_email/${email}`);
  }

  resetPassword(email: string, password: string): Observable<string> {
    return this.http.put<string>(
      `${this.baseUrl}/reset`,
      {
        email: email,
        password: password,
      },
      {
        responseType: 'text' as 'json',
      }
    );
  }
}
