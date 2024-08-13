import { Room } from './Room';

export class Seat {
  id: number;
  room: Room;
  seatNumber: string;
  booked: boolean;
  constructor(id: number, room: Room, seatNumber: string, booked: boolean) {
    this.id = id;
    this.room = room;
    this.seatNumber = seatNumber;
    this.booked = booked;
  }
}
