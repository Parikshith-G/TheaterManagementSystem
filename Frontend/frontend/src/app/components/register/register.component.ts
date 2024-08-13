import { Component, inject } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { UserRegistration } from '../../models/user-registration';
import { ToastrService } from 'ngx-toastr';
import { ConnectorService } from '../../services/connector/connector.service';
import { Router, RouterLink } from '@angular/router';
import { OtpService } from '../../services/otp/otp.service';

import { CommonModule } from '@angular/common';
import { UserService } from '../../services/users/user.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, CommonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  step: number = 1;
  userForms: FormGroup = new FormGroup({
    username: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.email, Validators.required]),
    password: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
      Validators.pattern(
        /^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,}$/
      ),
    ]),
    phone: new FormControl('', []),
  });

  isOtpSent: boolean = false;
  registeredObject: any;
  registerObj: UserRegistration;
  showRegister: boolean = false;
  constructor(
    private toastr: ToastrService,
    private connector: ConnectorService,
    private router: Router,
    private otpService: OtpService,
    private userService: UserService
  ) {
    this.registerObj = new UserRegistration('', '', '', '');
  }
  submitHandler() {
    if (this.userForms.valid) {
      this.toastr.info('Signing up....', 'Please Wait');
      this.registerObj = new UserRegistration(
        this.userForms.get('username')?.value,
        this.userForms.get('email')?.value,
        this.userForms.get('password')?.value,
        this.userForms.get('phone')?.value
      );
      this.connector.doSignUp(this.registerObj).subscribe({
        next: (val) => {
          this.toastr.success(
            'You have been successfully signed up....',
            'Please Log in'
          );
          this.registeredObject = val;
          this.router.navigateByUrl('/login');
        },
        error: (error) => {
          if (error.error.status == '403') {
            this.toastr.error('Invalid credentials', 'Login Error');
          } else {
            this.toastr.error(
              'Failed to login. Please try again later.',
              'Error'
            );
          }
        },
      });
    } else {
      if (this.userForms.get('username')?.invalid) {
        this.toastr.error('Username must be alteast 5 characters');
      }
      if (this.userForms.get('email')?.invalid) {
        this.toastr.error('Please give a valid email');
      }
      if (this.userForms.get('password')?.invalid) {
        this.toastr.error(
          'Password isnt strong enough(min 8 characters, 1 uppercase, lowercase, number, special character necessary)'
        );
      }
    }
  }
  sendOtp() {
    const email = this.userForms.get('email')?.value;
    this.userService.dublicateEmail(email).subscribe({
      next: (dubs) => {
        if (dubs) {
          this.toastr.error('Email already registered', 'Error');
        } else {
          this.otpService.generateOtp(email).subscribe((response) => {
            this.step = 2;
            this.toastr.success('Otp sent', 'Success');
          });
        }
      },

      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }
  onEmailBlur() {
    this.sendOtp();
  }

  validateOtp(otp: string) {
    this.otpService
      .validateOtp(this.userForms.get('email')?.value, otp)
      .subscribe({
        next: (isValid) => {
          if (isValid) {
            this.step = 1;
            this.showRegister = true;
            this.toastr.success('Email validated', 'Success');
            this.otpService
              .sendDeleteOtpSignal(this.userForms.get('email')?.value)
              .subscribe({
                next: () => {},

                error: (err) => {
                  this.toastr.error(err.error.message, 'Error');
                },
              });
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
