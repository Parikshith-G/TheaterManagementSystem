import { HttpClient } from '@angular/common/http';
import { Component, HostListener, OnInit } from '@angular/core';
import {
  FormGroup,
  FormBuilder,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { OrderServiceService } from './order-service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Booking } from '../../models/Booking';
import { PaymentService } from '../../services/payment/payment.service';
import { AuthService } from '../../services/auth/auth.service';
import { BookingService } from '../../services/booking/booking.service';
import { ToastrService } from 'ngx-toastr';

declare var Razorpay: any;

@Component({
  selector: 'app-payment',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css'],
})
export class PaymentComponent implements OnInit {
  paymentForm: FormGroup = new FormGroup({});
  paymentId: string = '';
  error: string = '';
  amount: number = 0;
  booking: Booking;
  options = {
    key: '',
    amount: '',
    name: 'Placeholder name',
    description: 'Payment',
    image:
      'https://cdn.dribbble.com/users/79252/screenshots/2825500/theatre-round-icon.png',
    order_id: '',
    handler: function (response: any) {
      var event = new CustomEvent('payment.success', {
        detail: response,
        bubbles: true,
        cancelable: true,
      });
      window.dispatchEvent(event);
    },
    prefill: {
      name: '',
      email: '',
      contact: '',
    },
    notes: {
      address: '',
    },
    theme: {
      color: '#3399cc',
    },
  };

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private orderService: OrderServiceService,
    private router: Router,
    private paymentService: PaymentService,
    private authService: AuthService,
    private bookingService: BookingService,
    private toastr: ToastrService
  ) {
    this.booking = new Booking(
      0,
      '',
      0,
      new Date(),
      0,
      0,
      '',
      new Date(),
      new Date(),
      new Date(),

      ''
    );
  }

  ngOnInit() {
    const { isValid, booking } = history.state;

    if (!isValid) {
      this.toastr.info('No payment sir', 'No No');
      this.router.navigate(['/theaters']);
    }

    this.paymentForm = this.fb.group({
      name: [{ value: '', disabled: true }],
      email: [{ value: '', disabled: true }],
      phone: [{ value: '', disabled: true }],
      amount: [{ value: '', disabled: true }],
    });

    this.amount = booking.totalPrice; // Assign booking amount to component variable
    this.paymentForm.patchValue({ amount: this.amount });
    this.paymentForm.patchValue({ name: this.authService.getUserName() });
    this.paymentForm.patchValue({ email: this.authService.getUserEmail() });
    this.paymentForm.patchValue({ phone: this.authService.getPhoneNumber() });
  }

  onSubmit(): void {
    if (this.paymentForm.invalid) {
      return;
    }
    this.paymentId = '';
    this.error = '';
    this.orderService.createOrder(this.paymentForm.value).subscribe({
      next: (data) => {
        this.options.key = data.secretId;
        this.options.order_id = data.razorpayOrderId;
        this.options.amount = data.applicationFee; // paise
        this.options.prefill.name = this.paymentForm.get('name')?.value;
        this.options.prefill.email = this.paymentForm.get('email')?.value;
        this.options.prefill.contact = this.paymentForm.get('phone')?.value;

        const rzp = new Razorpay(this.options);
        rzp.open();

        rzp.on(
          'payment.failed',
          (response: {
            error: {
              code: any;
              description: any;
              source: any;
              step: any;
              reason: any;
              metadata: { order_id: any; payment_id: any };
            };
          }) => {
            console.log(response);
            this.error = response.error.reason;
          }
        );
      },

      error: (err) => {
        this.error = err.error.message;
      },
    });
  }

  @HostListener('window:payment.success', ['$event'])
  onPaymentSuccess(event: { detail: any; type: any }): void {
    if (event.type === 'payment.failure') {
      this.toastr.error('Please book again', 'Something went wrong');
      return;
    }

    this.booking = this.paymentService.getBooking();
    this.booking.status = 'Confirmed';
    this.booking.userMail = this.paymentForm.get('email')?.value;
    this.booking.userName = this.paymentForm.get('name')?.value;
    this.bookingService.createBooking(this.booking).subscribe({
      next: (response) => {
        this.toastr.success(
          `Ticket downloaded and send to email ${this.booking.userMail}`,
          'Success'
        );
        const blob = new Blob([response], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = url;
        a.download = 'ticket.pdf';
        document.body.appendChild(a);
        a.click();

        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },

      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
    this.router.navigateByUrl('/theaters');
  }
}
