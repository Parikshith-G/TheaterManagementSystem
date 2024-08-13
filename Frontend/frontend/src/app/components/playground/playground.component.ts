import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-playground',
  standalone: true,
  imports: [],
  templateUrl: './playground.component.html',
  styleUrl: './playground.component.css',
})
export class PlaygroundComponent implements OnInit {
  token: string | null = '';
  name: string | null = '';
  email: string | null = '';
  pno: string | null = '';
  constructor(private authService: AuthService, private router: Router) {}
  ngOnInit(): void {
    this.token = this.authService.getToken();
    this.name = this.authService.getUserName();
    this.email = this.authService.getUserEmail();
    this.pno = this.authService.getPhoneNumber();
  }
  logoutHandler() {
    this.authService.clearToken();
    this.router.navigate(['/login']);
  }
}
