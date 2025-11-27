import { Routes } from '@angular/router';
import { adminGuard } from '../../core/guards/auth.guard';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./shared/admin-layout.component').then(m => m.AdminLayoutComponent),
    canActivate: [adminGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'movies',
        loadComponent: () => import('./movies/movie-list.component').then(m => m.MovieListComponent)
      },
      {
        path: 'movies/add',
        loadComponent: () => import('./movies/movie-form.component').then(m => m.MovieFormComponent)
      },
      {
        path: 'movies/edit/:id',
        loadComponent: () => import('./movies/movie-form.component').then(m => m.MovieFormComponent)
      },
      {
        path: 'shows',
        loadComponent: () => import('./shows/show-list.component').then(m => m.ShowListComponent)
      },
      {
        path: 'shows/add',
        loadComponent: () => import('./shows/show-form.component').then(m => m.ShowFormComponent)
      },
      {
        path: 'shows/edit/:id',
        loadComponent: () => import('./shows/show-form.component').then(m => m.ShowFormComponent)
      },
      {
        path: 'events',
        loadComponent: () => import('./events/event-list.component').then(m => m.EventListComponent)
      },
      {
        path: 'events/add',
        loadComponent: () => import('./events/event-form.component').then(m => m.EventFormComponent)
      },
      {
        path: 'events/edit/:id',
        loadComponent: () => import('./events/event-form.component').then(m => m.EventFormComponent)
      },
      {
        path: 'venues',
        loadComponent: () => import('./venues/venue-list.component').then(m => m.VenueListComponent)
      },
      {
        path: 'venues/add',
        loadComponent: () => import('./venues/venue-form.component').then(m => m.VenueFormComponent)
      },
      {
        path: 'venues/edit/:id',
        loadComponent: () => import('./venues/venue-form.component').then(m => m.VenueFormComponent)
      },
      {
        path: 'bookings',
        loadComponent: () => import('./bookings/bookings.component').then(m => m.BookingsComponent)
      },
      {
        path: 'users',
        loadComponent: () => import('./users/users.component').then(m => m.UsersComponent)
      }
    ]
  }
];
