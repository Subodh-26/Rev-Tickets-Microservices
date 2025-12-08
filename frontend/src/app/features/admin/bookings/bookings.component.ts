import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-admin-bookings',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-[#0a0f1e] p-8">
      <div class="max-w-7xl mx-auto">
        <div class="mb-8">
          <h1 class="text-4xl font-bold mb-2">All Bookings</h1>
          <p class="text-gray-400">Manage all customer bookings</p>
        </div>

        <!-- Bookings Table -->
        <div class="bg-[#121212] rounded-2xl border border-white/5 overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full">
              <thead class="bg-[#1a1a1a] border-b border-white/10">
                <tr>
                  <th class="px-6 py-4 text-left text-sm font-semibold text-gray-400">Booking ID</th>
                  <th class="px-6 py-4 text-left text-sm font-semibold text-gray-400">Reference</th>
                  <th class="px-6 py-4 text-left text-sm font-semibold text-gray-400">User</th>
                  <th class="px-6 py-4 text-left text-sm font-semibold text-gray-400">Email</th>
                  <th class="px-6 py-4 text-left text-sm font-semibold text-gray-400">Seats</th>
                  <th class="px-6 py-4 text-left text-sm font-semibold text-gray-400">Amount</th>
                  <th class="px-6 py-4 text-left text-sm font-semibold text-gray-400">Status</th>
                  <th class="px-6 py-4 text-left text-sm font-semibold text-gray-400">Payment</th>
                  <th class="px-6 py-4 text-left text-sm font-semibold text-gray-400">Date</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-white/5">
                @if (loading) {
                  <tr>
                    <td colspan="9" class="px-6 py-12 text-center text-gray-400">
                      <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-red-600 border-t-transparent"></div>
                      <p class="mt-4">Loading bookings...</p>
                    </td>
                  </tr>
                } @else if (bookings.length === 0) {
                  <tr>
                    <td colspan="9" class="px-6 py-12 text-center text-gray-400">
                      No bookings found
                    </td>
                  </tr>
                } @else {
                  @for (booking of bookings; track booking.bookingId) {
                    <tr class="hover:bg-white/5 transition-colors">
                      <td class="px-6 py-4 text-sm text-white">{{ booking.bookingId }}</td>
                      <td class="px-6 py-4 text-sm text-white font-mono">{{ booking.bookingReference }}</td>
                      <td class="px-6 py-4 text-sm text-white">{{ booking.user?.fullName || 'User #' + booking.userId }}</td>
                      <td class="px-6 py-4 text-sm text-gray-400">{{ booking.user?.email || '-' }}</td>
                      <td class="px-6 py-4 text-sm text-white">{{ booking.totalSeats }}</td>
                      <td class="px-6 py-4 text-sm text-white">₹{{ booking.totalAmount }}</td>
                      <td class="px-6 py-4">
                        <span [ngClass]="{
                          'bg-green-500/20 text-green-400 border-green-500/50': booking.bookingStatus === 'CONFIRMED',
                          'bg-yellow-500/20 text-yellow-400 border-yellow-500/50': booking.bookingStatus === 'PENDING',
                          'bg-red-500/20 text-red-400 border-red-500/50': booking.bookingStatus === 'CANCELLED',
                          'bg-blue-500/20 text-blue-400 border-blue-500/50': booking.bookingStatus === 'COMPLETED'
                        }" class="px-3 py-1 rounded-full text-xs font-semibold border">
                          {{ booking.bookingStatus }}
                        </span>
                      </td>
                      <td class="px-6 py-4">
                        <span [ngClass]="{
                          'bg-green-500/20 text-green-400 border-green-500/50': booking.paymentStatus === 'PAID',
                          'bg-yellow-500/20 text-yellow-400 border-yellow-500/50': booking.paymentStatus === 'PENDING',
                          'bg-red-500/20 text-red-400 border-red-500/50': booking.paymentStatus === 'FAILED',
                          'bg-gray-500/20 text-gray-400 border-gray-500/50': booking.paymentStatus === 'REFUNDED'
                        }" class="px-3 py-1 rounded-full text-xs font-semibold border">
                          {{ booking.paymentStatus }}
                        </span>
                      </td>
                      <td class="px-6 py-4 text-sm text-gray-400">{{ booking.bookingDate | date:'short' }}</td>
                    </tr>
                  }
                }
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  `
})
export class BookingsComponent implements OnInit {
  bookings: any[] = [];
  loading = true;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadBookings();
  }

  loadBookings() {
    this.loading = true;
    const token = localStorage.getItem('token');
    const headers = { 'Authorization': `Bearer ${token}` };
    
    this.http.get<any>(`${environment.apiUrl}/admin/bookings`, { headers }).subscribe({
      next: (response) => {
        console.log('Admin bookings response:', response);
        this.bookings = response.data || [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading admin bookings:', err);
        this.bookings = [];
        this.loading = false;
      }
    });
  }
}
