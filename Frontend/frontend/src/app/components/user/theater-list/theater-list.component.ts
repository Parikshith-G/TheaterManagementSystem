import { Component, OnInit } from '@angular/core';

import { CommonModule } from '@angular/common';
import { NavigationExtras, Router, RouterLink } from '@angular/router';
import { Theater } from '../../../models/Theater';
import { TheaterService } from '../../../services/theaters/theater.service';
import { FormsModule } from '@angular/forms';
import { MovieService } from '../../../services/movies/movie.service';
import { _Movie } from './_Movie';
import { ShowService } from '../../../services/shows/show.service';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Booking } from '../../../models/Booking';
import { Room } from '../../../models/Room';
import { SeatStatusDialogComponent } from '../../seat-status-dialog/seat-status-dialog.component';
import { RoomService } from '../../../services/room/room.service';
import { PaymentService } from '../../../services/payment/payment.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-theater-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './theater-list.component.html',
  styleUrl: './theater-list.component.css',
})
export class TheaterListComponent implements OnInit {
  searchTerm: string = '';

  theaters: Theater[] = [];
  container: _Movie[] = [];
  searchList: _Movie[] = [];
  booking: Booking;

  ngOnInit(): void {
    this.theaterService.getTheaters().subscribe({
      next: (val) => {
        return (this.theaters = val);
      },

      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });

    this.showService.getShows().subscribe({
      next: (show) => {
        for (let s of show) {
          this.movieService.getMovieById(s.movieId).subscribe({
            next: (mov) => {
              this.roomService.getRoomById(s.roomId).subscribe({
                next: (room) =>
                  this.theaterService.getTheaterById(room.theaterId).subscribe({
                    next: (val) => {
                      const date = new Date(s.startTime);
                      const day = this.padZero(date.getDate());
                      const month = this.padZero(date.getMonth() + 1); // getMonth() is zero-based
                      const hours = this.padZero(date.getHours());
                      const minutes = this.padZero(date.getMinutes());

                      const _date = `${day}-${month}`;
                      const _month = `${hours}-${minutes}`;
                      const m = {
                        movieId: mov.id,
                        movieName: mov.title,
                        roomId: s.roomId,
                        price: s.price,
                        location: val.location,
                        ddmm: _date,
                        hhtt: _month,
                      };
                      this.container.push(m);
                    },

                    error: (err) => {
                      this.toastr.error(err.error.message, 'Error');
                    },
                  }),
                error: (err) => {
                  this.toastr.error(err.error.message, 'Error');
                },
              });
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
  padZero(value: number): string {
    return value < 10 ? `0${value}` : value.toString();
  }
  constructor(
    private theaterService: TheaterService,
    private movieService: MovieService,
    private showService: ShowService,
    private dialog: MatDialog,
    private roomService: RoomService,
    private paymentService: PaymentService,
    private router: Router,
    private toastr: ToastrService
  ) {
    this.booking = new Booking(
      0,
      '',
      0,
      new Date(),
      0,
      0,
      'Pending',
      new Date(),
      new Date(),
      new Date(),
      ''
    );
  }
  selectRoom(room: Room): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = '250px';
    dialogConfig.position = { top: '100px' };
    dialogConfig.data = {
      seatStatuses: room.seatStatuses,
      roomId: room.id,
      roomName: room.name,
    };

    const dialogRef = this.dialog.open(SeatStatusDialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe({
      next: (result) => {
        if (result) {
          this.roomService.getRoomById(result[1]).subscribe({
            next: (room) => {
              const showId = room.showIds[0];
              this.showService.getShowById(showId).subscribe({
                next: (show) => {
                  this.booking = new Booking(
                    0,
                    '',
                    show.id,
                    new Date(),
                    show.price,
                    result[0],
                    'Pending',
                    new Date(),
                    new Date(),
                    show.startTime,
                    ''
                  );
                  const val: NavigationExtras = {
                    state: { isValid: true, booking: this.booking },
                  };
                  this.router.navigateByUrl('/payment', val);
                },

                error: (err) => {
                  this.toastr.error(err.error.message, 'Error');
                },
              });
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

  filterTheaters() {
    this.searchList = [];

    for (let val of this.container) {
      if (
        this.searchTerm.length > 0 &&
        val.movieName.substring(0, this.searchTerm.length).toLowerCase() ===
          this.searchTerm.toLowerCase()
      ) {
        this.searchList.push(val);
      }
    }
  }

  selectItem(roomId: number) {
    this.roomService.getRoomById(roomId).subscribe({
      next: (r) => {
        this.selectRoom(r);
      },

      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }
}
