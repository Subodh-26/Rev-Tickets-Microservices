import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: 'movies',
    loadChildren: () => import('./features/movies/movies.routes').then(m => m.MOVIE_ROUTES)
  },
  {
    path: 'events',
    loadChildren: () => import('./features/events/events.routes').then(m => m.EVENT_ROUTES)
  },
  {
    path: 'bookings',
    loadChildren: () => import('./features/bookings/bookings.routes').then(m => m.BOOKING_ROUTES)
  },
  {
    path: 'payment',
    loadComponent: () => import('./components/payment/payment.component').then(m => m.PaymentComponent)
  },
  {
    path: 'profile',
    loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent)
  },
  {
    path: 'admin',
    loadChildren: () => import('./features/admin/admin.routes').then(m => m.ADMIN_ROUTES)
  },
  {
    path: '**',
    redirectTo: ''
  }
];
