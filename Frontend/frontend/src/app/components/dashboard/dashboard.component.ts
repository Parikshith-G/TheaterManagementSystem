import { CommonModule, NgClass } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth/auth.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MoviesComponent } from '../admin/movies/movies.component';
import { RoomsComponent } from '../admin/rooms/rooms.component';
import { ShowsComponent } from '../admin/shows/shows.component';
import { TheatersComponent } from '../admin/theaters/theaters.component';
import { AllUsersNoAdminsComponent } from '../admin/all-users-no-admins/all-users-no-admins.component';
import { AllAdminsComponent } from '../admin/all-admins/all-admins.component';
import { AppComponent } from '../../app.component';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    NgClass,
    RouterLink,
    MoviesComponent,
    CommonModule,
    RoomsComponent,
    ShowsComponent,
    TheatersComponent,
    AllUsersNoAdminsComponent,
    AllAdminsComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit {
  status = false;
  flag: number = 0;
  userName: string | null = null;
  role: string = '';
  isClicked: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private appcomponent: AppComponent,
    private toastr: ToastrService
  ) {
    this.userName = this.authService.getUserName();
    this.role = this.authService.getUserRole();
    this.appcomponent.getIcon();
  }
  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe({
      next: (params) => {
        if (params['role'] === 'GOD') {
          if (this.authService.getUserRole() === 'GOD') {
            this.flag = 5;
          } else {
            this.flag = 0;
          }
        } else {
          this.flag = +params['flag'] || 0;
        }
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }
  roleSwapper() {
    this.isClicked = !this.isClicked;
  }
  addToggle() {
    this.status = !this.status;
  }

  clickHandler() {
    this.authService.clearToken();
    this.router.navigateByUrl('/login');
  }
}
