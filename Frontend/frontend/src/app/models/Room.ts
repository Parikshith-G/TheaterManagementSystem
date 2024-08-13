import SeatStatus from './seat-status/seat-status.module';

export class Room {
  id: number;
  theaterId: number;
  name: string;
  capacity: number;
  showIds: number[];
  seatStatuses: SeatStatus[];
  availableSeats: number;
  showDetails?: ShowDetails;
  constructor(
    id: number,
    theaterId: number,
    name: string,
    capacity: number,
    showIds: number[],
    seatStatuses: SeatStatus[],
    availableSeats: number,
    showDetails?: ShowDetails
  ) {
    this.id = id;
    this.theaterId = theaterId;
    this.name = name;
    this.capacity = capacity;
    this.showIds = showIds;
    this.seatStatuses = seatStatuses;
    this.availableSeats = availableSeats;
    this.showDetails = showDetails;
  }
}

interface ShowDetails {
  movieTitle: string;
  startTime: Date;
  price: number;
}
