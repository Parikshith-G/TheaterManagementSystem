import { Component, Inject, NgModule } from '@angular/core';
import SeatStatus from '../../models/seat-status/seat-status.module';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';
@Component({
  selector: 'app-seat-status-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './seat-status-dialog.component.html',
  styleUrl: './seat-status-dialog.component.css',
})
export class SeatStatusDialogComponent {
  constructor(
    public toastr: ToastrService,
    public dialogRef: MatDialogRef<SeatStatusDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      seatStatuses: SeatStatus[];
      roomId: number;
      roomName: string;
    }
  ) {}

  onSeatClick(seat: SeatStatus) {
    if (!seat.booked) {
      // Close the dialog and navigate to the payment page
      
      this.dialogRef.close([seat.seatId, this.data.roomId]);
    } else {
      this.toastr.error('Please select another seat', 'Seat already booked');
    }
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
