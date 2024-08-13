export class Booking {
  id: number;
  userMail: string;
  showId: number;
  bookingDate: Date;
  totalPrice: number;
  seatId: number;
  status: string;
  createdAt: Date;
  updatedAt: Date;
  showTime: Date;
  userName: string;

  constructor(
    id: number,
    userMail: string,
    showId: number,
    bookingDate: Date,

    totalPrice: number,
    seatId: number,
    status: string,
    createdAt: Date,
    updatedAt: Date,
    showTime: Date,
    userName: string
  ) {
    this.id = id;
    this.userMail = userMail;
    this.showId = showId;
    this.bookingDate = bookingDate;
    this.totalPrice = totalPrice;
    this.seatId = seatId;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.showTime = showTime;
    this.userName = userName;
  }
}
