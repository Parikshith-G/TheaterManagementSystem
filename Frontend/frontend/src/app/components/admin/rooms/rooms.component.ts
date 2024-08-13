import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbModalRef, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Room } from '../../../models/Room';
import { Theater } from '../../../models/Theater';
import { TheaterService } from '../../../services/theaters/theater.service';
import { RoomService } from '../../../services/room/room.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-rooms',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './rooms.component.html',
  styleUrl: './rooms.component.css',
})
export class RoomsComponent implements OnInit {
  rooms: Room[] = [];
  theaters: Theater[] = [];
  newRoom: Room = new Room(0, 0, '', 0, [], [], 0);
  private createRoomModalRef: NgbModalRef | undefined;

  @ViewChild('createRoomModal') createRoomModal: any;

  constructor(
    private theaterService: TheaterService,
    private modalService: NgbModal,
    private roomService: RoomService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadRooms();
    this.loadTheaters();
  }

  loadRooms(): void {
    this.roomService.getRooms().subscribe({
      next: (rooms) => {
        this.rooms = rooms;
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
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

  openCreateRoomModal(): void {
    this.createRoomModalRef = this.modalService.open(this.createRoomModal);
  }

  createRoom(): void {
    if (
      this.newRoom.capacity <= 0 ||
      this.newRoom.name.length <= 0 ||
      this.newRoom.name.length <= 0
    ) {
      this.toastr.error('All fields are required', 'Error');
      return;
    }
    this.roomService.addRoomToTheater(this.newRoom).subscribe({
      next: () => {
        this.loadRooms();
        if (this.createRoomModalRef) {
          this.createRoomModalRef.close();
        }
        this.newRoom = new Room(0, 0, '', 0, [], [], 0);
        this.toastr.success('Room created successfully', 'Success');
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }

  deleteRoom(id: number) {
    this.roomService.deleteRoomById(id).subscribe({
      next: (response) => {
        this.toastr.success('Room deleted successfully', 'Success');
      },
      complete: () => {
        this.loadRooms();
        this.loadTheaters();
      },
      error: (err) => {
        this.toastr.error(err.error.message, 'Error');
      },
    });
  }
}
