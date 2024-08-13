import SeatStatus from "./seat-status/seat-status.module";

export default class Placeholder {
  id: number;
  theaterId: number;
  name: string;
  capacity: number;
  showIds: number[];
  seatStatuses: SeatStatus[];
  availableSeats: number;

  constructor(
    id: number,
    theaterId: number,
    name: string,
    capacity: number,
    showIds: number[],
    seatStatuses: SeatStatus[],
    availableSeats: number
  ) {
    this.id = id;
    this.theaterId = theaterId;
    this.name = name;
    this.capacity = capacity;
    this.showIds = showIds;
    this.seatStatuses = seatStatuses;
    this.availableSeats = availableSeats;
  }
}
