export default class SeatStatus {
  seatId: number;
  booked: Boolean;
  constructor(seatId: number, booked: Boolean) {
    this.seatId = seatId;
    this.booked = booked;
  }
}
