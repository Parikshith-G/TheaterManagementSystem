import { CommonModule, NgClass, NgStyle } from '@angular/common';
import { Component, ElementRef, HostListener, OnInit } from '@angular/core';
import {
  FormGroup,
  FormBuilder,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-logging-in',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, NgStyle, NgClass, RouterLink],
  templateUrl: './logging-in.component.html',
  styleUrl: './logging-in.component.css',
})
export class LoggingInComponent {
onLogin() {
throw new Error('Method not implemented.');
}
onSignUp() {
throw new Error('Method not implemented.');
}
  showSignUp = false; // Flag to toggle between login and signup forms
  loginForm: FormGroup;
  signUpForm: FormGroup;

  constructor(private fb: FormBuilder, private elementRef: ElementRef) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });

    this.signUpForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  toggleSignUp() {
    this.showSignUp = !this.showSignUp; // Toggle the flag to show/hide signup form
  }

  onSubmitLogin() {
    if (this.loginForm.valid) {
      console.log(this.loginForm.value);
      // Implement your login logic here
    }
  }

  onSubmitSignUp() {
    if (this.signUpForm.valid) {
      console.log(this.signUpForm.value);
      // Implement your signup logic here

      // Optionally, you can close the modal after successful signup
      this.showSignUp = false;
    }
  }

  @HostListener('document:click', ['$event'])
  closeModalOnOutsideClick(event: any) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      // Clicked outside the modal, close it
      this.showSignUp = false;
    }
  }
}
