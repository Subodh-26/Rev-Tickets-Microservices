import { Routes } from '@angular/router';
import { authGuard } from '../../core/guards/auth.guard';

export const BOOKING_ROUTES: Routes = [
  {
    path: 'select-show',
    loadComponent: () => import('./select-show/select-show.component').then(m => m.SelectShowComponent)
  },
  {
    path: 'seat-selection',
    loadComponent: () => import('./seat-selection/seat-selection.component').then(m => m.SeatSelectionComponent)
  },
  {
    path: 'payment',
    loadComponent: () => import('../../components/payment/payment.component').then(m => m.PaymentComponent),
    canActivate: [authGuard]
  },
  {
    path: 'my-bookings',
    loadComponent: () => import('./my-bookings/my-bookings.component').then(m => m.MyBookingsComponent),
    canActivate: [authGuard]
  }
];
