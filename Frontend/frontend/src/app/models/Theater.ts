import { Room } from './Room';

export class Theater {
  id: number;
  name: string;
  location: string;
  roomsIdx: number[];
  constructor(id: number, name: string, location: string, roomsIdx: number[]) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.roomsIdx = roomsIdx;
  }
}
