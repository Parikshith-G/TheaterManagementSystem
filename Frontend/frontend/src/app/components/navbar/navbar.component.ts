import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent implements OnInit {
  isAuthenticated: boolean = false;
  isAdmin: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.isAuthenticated = !!this.authService.getToken();
    this.isAdmin = this.authService.getUserRole() === 'ADMIN';
  }

  logout() {
    this.authService.clearToken();
    this.router.navigate(['/login']);
  }
}
