import { Component, inject, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Movie } from '../../../models/Movie';
import { Room } from '../../../models/Room';
import { Show } from '../../../models/Show';
import { MovieService } from '../../../services/movies/movie.service';
import { RoomService } from '../../../services/room/room.service';
import { ShowService } from '../../../services/shows/show.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-shows',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './shows.component.html',
  styleUrl: './shows.component.css',
})
export class ShowsComponent implements OnInit {
  shows: Show[] = [];
  movies: Movie[] = [];
  rooms: Room[] = [];
  newShow: Show = new Show(0, 0, 0, new Date(), new Date(), 0);

  constructor(
    private showService: ShowService,
    private movieService: MovieService,
    private roomService: RoomService,
    private modalService: NgbModal,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadShows();
    this.loadMovies();
    this.loadRooms();
  }

  loadShows(): void {
    this.showService.getShows().subscribe({
      next: (shows) => {
        this.shows = shows;
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }

  loadMovies(): void {
    this.movieService.getMovies().subscribe({
      next: (movies) => {
        this.movies = movies;
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }

  loadRooms(): void {
    this.roomService.getRooms().subscribe({
      next: (rooms) => {
        this.rooms = rooms.filter((room) => room.showIds.length === 0);
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }

  open(content: any): void {
    this.modalService.open(content);
  }

  calculateEndTime(): void {
    const selectedMovie = this.movies.find(
      (movie) => movie.id === this.newShow.movieId
    );
    if (this.newShow.startTime && selectedMovie) {
      // Convert startTime to a Date object if it's a string
      if (typeof this.newShow.startTime === 'string') {
        this.newShow.startTime = new Date(this.newShow.startTime);
      }
      this.newShow.endTime = new Date(
        this.newShow.startTime.getTime() + selectedMovie.duration * 60000
      );
    }
  }

  addShow(): void {
    if (
      !this.newShow.startTime ||
      !this.newShow.movieId ||
      !this.newShow.roomId
    ) {
      this.toastr.error('Please fill in all required fields', 'Error');
      return;
    }
    if (this.newShow.price <= 0) {
      this.toastr.error("We don't have a money printing press", 'Error');
      return;
    }
    this.showService.createShow(this.newShow).subscribe({
      complete: () => {
        this.loadShows();
        this.loadRooms();
        this.newShow = new Show(0, 0, 0, new Date(), new Date(), 0);
        this.modalService.dismissAll();
        this.toastr.success('Show added successfully', 'Succcess');
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }

  deleteShow(id: number): void {
    this.showService.deleteShow(id).subscribe({
      complete: () => {
        this.loadShows();
        this.loadRooms();
        this.toastr.success('Show deleted successfully', 'Succcess');
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }
}
