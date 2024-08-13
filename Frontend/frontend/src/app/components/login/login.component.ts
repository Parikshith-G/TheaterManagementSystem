import { Component, inject, OnInit } from '@angular/core';
import UserLogin from '../../models/user-login';
import {
  EmailValidator,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { ConnectorService } from '../../services/connector/connector.service';
import { AuthService } from '../../services/auth/auth.service';
import { Router, RouterLink } from '@angular/router';
import { GoldToastrComponent } from '../admin/gold-toastr/gold-toastr.component';
import { CommonModule } from '@angular/common';
import { OtpService } from '../../services/otp/otp.service';
import { UserService } from '../../services/users/user.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    GoldToastrComponent,
    CommonModule,
    FormsModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  didForget: boolean = false;
  isOtpSent: boolean = false;
  isOtpValidated: boolean = false;
  forgotten: boolean = false;
  isItForgotten: boolean = false;

  updateEmailHandler() {
    if (this.didForget && !this.isOtpValidated) {
      this.forgotPasswordHandler();
    }
  }
  forgotPasswordHandler() {
    this.didForget = true;
    this.forgotten = true;
    const email = this.userForms.get('email')?.value;
    if (email.length == 0) {
      this.toastr.error('Please enter your email', 'Error');
      return;
    }
    this.userService.doesEmailExist(email).subscribe({
      next: (res) => {
        if (!res) {
          this.toastr.error('Email does not exist', 'Error');
        } else {
          this.otpService.generateOtp(email).subscribe(() => {
            this.toastr.success(`Otp send to the email ${email}`, 'Success');
            this.isOtpSent = true;
          });
        }
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }

  userForms: FormGroup = new FormGroup({
    email: new FormControl('', [Validators.required]),
    password: new FormControl('', []),
    newPassword: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
      Validators.pattern(
        /^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,}$/
      ),
    ]),

    confirmNewPassword: new FormControl('', []),
  });

  loginObj: UserLogin;

  loggedInObj: any;
  constructor(
    private toastr: ToastrService,
    private connector: ConnectorService,
    private authService: AuthService,
    private router: Router,
    private otpService: OtpService,
    private userService: UserService
  ) {
    this.loginObj = new UserLogin('', '');
  }

  showCustomToast() {
    const toastrTemplate = `
    <div class="custom-toastr">
      <div class="toast-header">
        <strong class="mr-auto">Logged in as God</strong>
      </div>
      <div class="toast-body">
       WIth great power comes great responsibility.
      </div>
    </div>
  `;
    this.toastr.show(toastrTemplate, '', {
      closeButton: true,
      enableHtml: true,
      tapToDismiss: false,
      toastClass: 'ngx-custom-toastr',
    });
  }
  loginHandler() {
    this.toastr.info('Logging in....', 'Please Wait');

    this.loginObj = new UserLogin(
      this.userForms.get('email')?.value,
      this.userForms.get('password')?.value
    );

    this.connector.doLogin(this.loginObj).subscribe({
      next: (val) => {
        this.loggedInObj = val;

        this.authService.setToken(this.loggedInObj.Token);
        const role = this.authService.getUserRole();
        if (role === 'USER') {
          this.toastr.success('Logged in successfully as User', 'Success');
          this.router.navigateByUrl('/theaters');
        } else if (role === 'ADMIN') {
          this.toastr.success('Logged in successfully as Admin', 'Success');
          this.router.navigateByUrl('/dashboard');
        } else {
          this.showCustomToast();
          this.router.navigateByUrl('/dashboard');
        }
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }
  signInHander(event: Event) {
    event.preventDefault();
    this.router.navigateByUrl('/signup');
  }

  validateOtp(otp: string) {
    const email = this.userForms.get('email')?.value;

    this.otpService.validateOtp(email, otp).subscribe({
      next: (isValid) => {
        if (isValid) {
          this.isItForgotten = true;
          this.isOtpValidated = true;
          this.isOtpSent = false;
          this.toastr.success('Email validated', 'Success');

          this.otpService.sendDeleteOtpSignal(email).subscribe({
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

  confirmPasswordReset() {
    if (this.userForms.get('newpassword')?.valid) {
      this.toastr.error(
        'Password must be min 8 characters, contain atleast 1 uppercase, 1 lowercase, 1 number, 1 special character',
        'Error'
      );
      return;
    }
    const newPassword = this.userForms.get('newPassword')?.value;
    const confirmNewPassword = this.userForms.get('confirmNewPassword')?.value;
    if (
      this.userForms.get('newPassword')?.valid &&
      confirmNewPassword === newPassword
    ) {
      this.userService
        .resetPassword(this.userForms.get('email')?.value, newPassword)
        .subscribe({
          next: (value) => {
            this.authService.setToken(value);
            this.toastr.success('Password reset successfully.', 'Success');
            this.router.navigateByUrl('/signup');
          },
          error: (err) => {
            this.toastr.error(err.error.message, 'Error');
          },
        });
    } else if (confirmNewPassword !== newPassword) {
      this.toastr.error('Passwords do not match', 'Error');
    } else {
      console.log(newPassword, confirmNewPassword);
      this.toastr.error('Passwords do not meet the requirements', 'Error');
    }
  }
}
