import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Movie } from '../../../models/Movie';
import { MovieService } from '../../../services/movies/movie.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-movies',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './movies.component.html',
  styleUrl: './movies.component.css',
})
export class MoviesComponent implements OnInit {
  movies: Movie[] = [];
  newMovie: Movie = new Movie(0, '', '', 0, '');

  constructor(
    private movieService: MovieService,
    private modalService: NgbModal,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadMovies();
  }

  loadMovies(): void {
    this.movieService.getMovies().subscribe((movies) => {
      this.movies = movies;
    });
  }

  open(content: any): void {
    this.modalService.open(content);
  }

  addMovie(): void {
    if (
      this.newMovie.description.length <= 0 ||
      this.newMovie.genre.length <= 0 ||
      this.newMovie.title.length <= 0
    ) {
      this.toastr.error('All fields are required', 'Error');
      return;
    }
    if (this.newMovie.duration <= 0) {
      this.toastr.error('Movie duration cannot be 0 or negative', 'Error');
      return;
    }
    this.movieService.createMovie(this.newMovie).subscribe({
      next: () => {
        this.loadMovies();
        this.newMovie = new Movie(0, '', '', 0, '');

        this.modalService.dismissAll();
        this.toastr.success('Movie added successfully', 'Success');
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }

  deleteMovie(id: number): void {
    this.movieService.deleteMovie(id).subscribe({
      next: () => {
        this.loadMovies();
        this.toastr.success('Movie deleted successfully', 'Success');
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }
}
