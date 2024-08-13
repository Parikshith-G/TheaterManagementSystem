import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ActivatedRoute, NavigationExtras, Router } from '@angular/router';
import { Booking } from '../../../models/Booking';
import { Movie } from '../../../models/Movie';
import { Room } from '../../../models/Room';
import { Show } from '../../../models/Show';
import { Theater } from '../../../models/Theater';
import { PaymentService } from '../../../services/payment/payment.service';
import { RoomService } from '../../../services/room/room.service';
import { ShowService } from '../../../services/shows/show.service';
import { TheaterService } from '../../../services/theaters/theater.service';
import { SeatStatusDialogComponent } from '../../seat-status-dialog/seat-status-dialog.component';
import { MovieService } from '../../../services/movies/movie.service';
import { FormsModule } from '@angular/forms';
import { MovieDetailsModalComponent } from '../movie-details-modal/movie-details-modal.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-theater-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './theater-detail.component.html',
  styleUrls: ['./theater-detail.component.css'],
})
export class TheaterDetailComponent implements OnInit {
  theater: Theater;
  rooms: Room[] = [];
  selectedRoom: Room;
  booking: Booking;
  show: Show;
  movie: Movie;
  searchQuery: string = '';
  searchResults: any[] = [];

  constructor(
    private route: ActivatedRoute,
    private theaterService: TheaterService,
    private roomService: RoomService,
    private dialog: MatDialog,
    private router: Router,
    private showService: ShowService,
    private movieService: MovieService,
    private paymentService: PaymentService,
    private modalService: NgbModal,
    private toastr: ToastrService
  ) {
    this.theater = new Theater(0, '', '', []);
    this.selectedRoom = new Room(0, 0, '', 0, [], [], 0);
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
    this.show = new Show(0, 0, 0, new Date(), new Date(), 0);
    this.movie = new Movie(0, '', '', 0, '');
  }

  ngOnInit(): void {
    const theaterId = this.route.snapshot.paramMap.get('id');

    if (theaterId) {
      this.theaterService.getTheaterById(+theaterId).subscribe({
        next: (data) => {
          this.theater = data;
          this.loadRoomsAndShows();
          console.log(this.theater);
        },

        error: (err) => {
          this.toastr.error(err.error.message, 'Error');
        },
      });
    }
  }

  loadRoomsAndShows(): void {
    for (let r of this.theater.roomsIdx) {
      this.roomService.getRoomById(r).subscribe({
        next: (rooms) => {
          this.rooms.push(rooms);

          this.rooms.forEach((room) => {
            this.roomService.getAvailableSeats(room.id).subscribe({
              next: (availableSeats) => {
                room.availableSeats = availableSeats;
              },

              error: (err) => {
                this.toastr.error(err.error.message, 'Error');
              },
            });

            if (room.showIds.length > 0) {
              const showId = room.showIds[0];
              this.showService.getShowById(showId).subscribe({
                next: (show) => {
                  this.movieService.getMovieById(show.movieId).subscribe({
                    next: (movie) => {
                      room.showDetails = {
                        movieTitle: movie.title,
                        startTime: show.startTime,
                        price: show.price,
                      };
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
          });
        },

        error: (err) => {
          this.toastr.error(err.error.message, 'Error');
        },
      });
    }
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
                  this.paymentService.setBooking(this.booking);
                  this.router.navigate(['/payment'], val);
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

  movieDetails(showId: number) {
    this.showService.getShowById(showId).subscribe({
      next: (show) =>
        this.movieService.getMovieById(show.movieId).subscribe({
          next: (movie) => {
            const modalRef = this.modalService.open(MovieDetailsModalComponent);
            modalRef.componentInstance.movieDetails = movie;
          },

          error: (err) => {
            this.toastr.error(err.error.message, 'Error');
          },
        }),

      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }
}
