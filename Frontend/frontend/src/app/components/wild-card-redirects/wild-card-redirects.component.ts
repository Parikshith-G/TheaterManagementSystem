import { Component, OnInit } from '@angular/core';

import { AuthService } from '../../services/auth/auth.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-wild-card-redirects',
  standalone: true,
  imports: [],
  templateUrl: './wild-card-redirects.component.html',
  styleUrl: './wild-card-redirects.component.css',
})
export class WildCardRedirectsComponent implements OnInit {
  constructor(
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {
    this.toastr.info(
      'No need to explore traveller, all lands have been found',
      "Don't we have an explorer here?"
    );
  }
  ngOnInit(): void {
    const token = this.authService.getToken();
    if (!token) {
      this.router.navigateByUrl('/login');
    } else {
      const role = this.authService.getUserRole();
      if (role === 'ADMIN' || role === 'GOD') {
        this.router.navigateByUrl('/dashboard');
      } else if (role === 'USER') {
        this.router.navigateByUrl('/theaters');
      } else {
        this.router.navigateByUrl('/login');
      }
    }
  }
}
