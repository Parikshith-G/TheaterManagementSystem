export class Movie {
  id: number;
  title: string;
  genre: string;
  duration: number;
  description: string;

  constructor(
    id: number,
    title: string,
    genre: string,
    duration: number,
    description: string
  ) {
    this.id = id;
    this.title = title;
    this.genre = genre;
    this.duration = duration;
    this.description = description;
  }
}
