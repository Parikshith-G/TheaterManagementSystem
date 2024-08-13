import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly jwtTokenKey = 'jwtToken';
  constructor() {}

  setToken(token: string): void {
    sessionStorage.setItem(this.jwtTokenKey, token);
  }

  getToken(): string | null {
    const token = sessionStorage.getItem(this.jwtTokenKey);
    if (token && this.isTokenExpired(token)) {
      this.clearToken();
      return null;
    }
    return token;
  }
  clearToken(): void {
    sessionStorage.removeItem(this.jwtTokenKey);
  }
  isTokenExpired(token: string): boolean {
    if (!token) {
      return true;
    }
    const tokenPayload = JSON.parse(atob(token.split('.')[1]));
    const expiry = new Date(tokenPayload.exp * 1000);
    return expiry <= new Date();
  }
  getUserRole(): string {
    const token = this.getToken();
    if (!token) {
      return '';
    }
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.role;
  }

  getUserName(): string {
    const token = this.getToken();
    if (!token) {
      return '';
    }
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.userName;
  }

  getUserEmail(): string {
    const token = this.getToken();
    if (!token) {
      return '';
    }
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.email;
  }

  getPhoneNumber(): string {
    const token = this.getToken();
    if (!token) {
      return '';
    }
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.phoneNumber;
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }
    return token.length > 0;
  }

  isAdmin(): boolean {
    return this.getUserRole() == 'ADMIN';
  }

  getUserId(): number {
    const token = this.getToken();
    if (!token) {
      return 0;
    }
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.id;
  }
}
