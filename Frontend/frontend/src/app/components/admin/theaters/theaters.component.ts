import { Component, OnInit, ViewChild } from '@angular/core';
import { Theater } from '../../../models/Theater';
import { TheaterService } from '../../../services/theaters/theater.service';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AppComponent } from '../../../app.component';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-theaters',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './theaters.component.html',
  styleUrl: './theaters.component.css',
})
export class TheatersComponent implements OnInit {
  theaters: Theater[] = [];
  newTheater: Theater = new Theater(0, '', '', []);
  private createTheaterModalRef: NgbModalRef | undefined;

  @ViewChild('createTheaterModal') createTheaterModal: any;

  constructor(
    private theaterService: TheaterService,
    private modalService: NgbModal,
    private ac: AppComponent,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadTheaters();
    this.ac.getIcon();
  }

  loadTheaters(): void {
    this.theaterService.getTheaters().subscribe({
      next: (theaters) => {
        this.theaters = theaters;
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }

  openCreateTheaterModal(): void {
    this.createTheaterModalRef = this.modalService.open(
      this.createTheaterModal
    );
  }

  createTheater(): void {
    if (!this.newTheater.name || !this.newTheater.location) {
      this.toastr.error('Please fill in all  fields!', 'Error');
      return;
    }
    this.theaterService.createTheater(this.newTheater).subscribe({
      next: () => {
        this.loadTheaters();
        if (this.createTheaterModalRef) {
          this.createTheaterModalRef.close();
        }
        this.newTheater = new Theater(0, '', '', []);
        this.toastr.success('Theater created successfully!', 'Success');
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }

  deleteTheater(id: number): void {
    this.theaterService.deleteTheater(id).subscribe({
      next: () => {
        this.loadTheaters();
        this.toastr.success('Theater deleted successfully!', 'Success');
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }
}
