import { Movie } from './Movie';
import { Room } from './Room';

export class Show {
  id: number;
  movieId: number;
  roomId: number;
  startTime: Date;
  endTime: Date;
  price: number;
  constructor(
    id: number,
    movieId: number,
    roomId: number,
    startTime: Date,
    endTime: Date,
    price: number
  ) {
    this.id = id;
    this.movieId = movieId;
    this.roomId = roomId;
    this.startTime = startTime;
    this.endTime = endTime;
    this.price = price;
  }
}
