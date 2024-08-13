import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth/auth.service';
import { NavbarComponent } from './components/navbar/navbar.component';

import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';
import { UserIconComponent } from './components/user/user-icon/user-icon.component';

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  imports: [
    RouterOutlet,
    NavbarComponent,
    CommonModule,
    FormsModule,
    UserIconComponent,
  ],
})
export class AppComponent implements OnInit {
  tkn: string | null = '';
  tkn1: string = '';
  constructor(private authService: AuthService, private router: Router) {}
  ngOnInit(): void {
    console.log('AppComponent');
    const role = this.authService.getUserRole();
    this.tkn = this.authService.getToken();

    // if (!(this.tkn === null || this.tkn === '')) {
    //   this.tkn1 = 'a';
    // }
    // if (role === 'USER') {
    //   this.router.navigateByUrl('/theaters');
    // } else if (role === 'ADMIN' || role === 'GOD') {
    //   this.router.navigateByUrl('/dashboard');
    // } else {
    //   this.router.navigateByUrl('/login');
    // }
  }
  getIcon() {}
  title: string = '';
}
