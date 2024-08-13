import { CommonModule } from '@angular/common';
import { Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { EmailValidator, FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth/auth.service';
import { Router } from '@angular/router';
import { UserService } from '../../../services/users/user.service';
import { UserRegistration } from '../../../models/user-registration';
import { ToastrService } from 'ngx-toastr';
import { User } from '../../../models/user';
import { OtpService } from '../../../services/otp/otp.service';

@Component({
  selector: 'app-user-icon',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-icon.component.html',
  styleUrl: './user-icon.component.css',
})
export class UserIconComponent {
  step = 1;
  showRegister: boolean = false;
  userObj: UserRegistration = new UserRegistration('', '', '', '');
  user: User = {
    id: 0,
    name: '',
    email: '',
    password: '',
    phone: '',
  };
  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router,
    private toastr: ToastrService,
    private otpService: OtpService
  ) {}

  loadUser() {
    this.user = {
      id: this.authService.getUserId(),
      name: this.authService.getUserName(),
      email: this.authService.getUserEmail(),
      password: '',
      phone: this.authService.getPhoneNumber(),
      // photo: '',
    };
  }

  showUserModal = false;
  showEditUserModal = false;

  openUserModal() {
    // this.loadUser();
    this.showUserModal = true;
  }

  closeUserModal(event: MouseEvent) {
    event.stopPropagation();
    this.step = 1;
    this.showUserModal = false;
  }

  openEditUserModal() {
    this.showUserModal = false;
    this.showEditUserModal = true;
  }

  closeEditUserModal(event: MouseEvent) {
    this.loadUser();
    event.stopPropagation();
    this.showEditUserModal = false;
  }

  deleteUser() {
    this.userService.deleteUser(this.user.id).subscribe({
      next: (val) => {
        if (val) {
          this.toastr.info('User deleted', 'Deleted');
          this.router.navigateByUrl('/login');
        } else {
          this.toastr.error('Something went wrong, please try again', 'Error');
        }
      },

      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
    this.closeUserModal(new MouseEvent('click'));
  }

  saveUser() {
    this.userService.updateUser(this.user).subscribe({
      next: (val) => {
        this.authService.setToken(val);
        this.loadUser();
        this.toastr.success('Updated', 'Success');
      },

      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
    this.user.name = '';
    this.user.email = '';
    this.user.password = '';
    this.user.phone = '';
    this.closeEditUserModal(new MouseEvent('click'));
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
  }

  sendOtp(email: string) {
    this.userService.dublicateEmail(email).subscribe({
      next: (dubs) => {
        if (dubs) {
          this.toastr.error('Email already registered', 'Error');
        } else {
          this.otpService.generateOtp(email).subscribe({
            next: (val) => {
              this.toastr.success('Otp has been sent', 'Success');
              this.step = 2;
            },

            error: (err) => {
              this.toastr.error(err.error.message, 'Error');
            },
          });
        }
      },

      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }
  navbarInit() {
    if (sessionStorage.getItem('jwtToken')) {
      this.loadUser();
      return true;
    }
    return false;
  }
  logout() {
    this.authService.clearToken();
    this.router.navigateByUrl('/login');
  }

  validateOtp(otp: string) {
    this.otpService.validateOtp(this.user.email, otp).subscribe({
      next: (isValid) => {
        if (isValid) {
          this.step = 1;
          this.showRegister = true;
          this.toastr.success('Email validated', 'Success');
        } else {
          this.toastr.error('OTP invalid', 'Error');
        }
      },

      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }
}
