import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Movie } from '../../../models/Movie';

@Component({
  selector: 'app-movie-details-modal',
  standalone: true,
  imports: [],
  templateUrl: './movie-details-modal.component.html',
  styleUrl: './movie-details-modal.component.css',
})
export class MovieDetailsModalComponent {
  @Input() movieDetails: Movie = new Movie(0, '', '', 0, '');

  constructor(public activeModal: NgbActiveModal) {}
}
