import { Routes } from '@angular/router';

export const MOVIE_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./movie-list/movie-list.component').then(m => m.MovieListComponent)
  },
  {
    path: ':id',
    loadComponent: () => import('./movie-details/movie-details.component').then(m => m.MovieDetailsComponent)
  }
];
