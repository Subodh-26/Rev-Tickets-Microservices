export interface Movie {
  id: number;
  title: string;
  description: string;
  genre: string;
  language: string;
  durationMinutes: number;
  parentalRating: string;
  releaseDate: string;
  displayImageUrl?: string;
  bannerImageUrl?: string;
  cast?: string;
  crew?: string;
  trailerUrl?: string;
  rating?: number;
  isActive: boolean;
}

export interface Event {
  id: number;
  title: string;
  description: string;
  category: string;
  eventDate: string;
  eventTime: string;
  durationMinutes: number;
  artistOrTeam: string;
  language: string;
  ageRestriction?: string;
  displayImageUrl?: string;
  bannerImageUrl?: string;
  trailerUrl?: string;
  isActive: boolean;
}

export interface Show {
  id: number;
  movie?: Movie;
  event?: Event;
  venue: Venue;
  showDate: string;
  showTime: string;
  screenName: string;
  format: string;
  language: string;
  basePrice: number;
  totalSeats: number;
  availableSeats: number;
  isActive: boolean;
}

export interface Venue {
  venueId: number;
  venueName: string;
  address: string;
  city: string;
  state: string;
  pincode: string;
  totalScreens: number;
  facilities?: any;
  isActive: boolean;
}

export interface Seat {
  id: number;
  showId: number;
  rowName: string;
  seatNumber: number;
  seatType: string;
  price: number;
  isBooked: boolean;
}

export interface Booking {
  id: number;
  bookingReference: string;
  show: Show;
  totalAmount: number;
  numberOfSeats: number;
  bookingStatus: string;
  bookingDate: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}
