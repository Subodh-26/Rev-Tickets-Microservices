import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="min-h-screen bg-[#0a0f1e] p-8">
      <div class="max-w-7xl mx-auto">
        <div class="mb-8">
          <h1 class="text-4xl font-bold mb-2">Admin Dashboard</h1>
          <p class="text-gray-400">Manage your movies, events, shows, and venues</p>
        </div>

        <!-- Stats Grid -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div class="bg-[#121212] rounded-2xl p-6 border border-white/5 text-center hover:border-red-500/30 transition-all">
            <p class="text-gray-400 text-sm mb-2">Total Users</p>
            <p class="text-4xl font-bold text-red-500">{{ stats?.totalUsers || 0 }}</p>
          </div>
          
          <div class="bg-[#121212] rounded-2xl p-6 border border-white/5 text-center hover:border-green-500/30 transition-all">
            <p class="text-gray-400 text-sm mb-2">Total Movies</p>
            <p class="text-4xl font-bold text-green-500">{{ stats?.totalMovies || 0 }}</p>
          </div>
          
          <div class="bg-[#121212] rounded-2xl p-6 border border-white/5 text-center hover:border-blue-500/30 transition-all">
            <p class="text-gray-400 text-sm mb-2">Total Bookings</p>
            <p class="text-4xl font-bold text-blue-500">{{ stats?.totalBookings || 0 }}</p>
          </div>
          
          <div class="bg-[#121212] rounded-2xl p-6 border border-white/5 text-center hover:border-yellow-500/30 transition-all">
            <p class="text-gray-400 text-sm mb-2">Total Revenue</p>
            <p class="text-4xl font-bold text-yellow-500">₹{{ stats?.totalRevenue || 0 }}</p>
          </div>
        </div>

        <!-- Booking Status -->
        <div class="bg-[#121212] rounded-2xl p-8 border border-white/5">
          <h2 class="text-2xl font-bold mb-6">Booking Status</h2>
          <div class="grid grid-cols-3 gap-6">
            <div class="text-center">
              <p class="text-green-500 text-4xl font-bold mb-2">{{ stats?.bookingsByStatus?.confirmed || 0 }}</p>
              <p class="text-gray-400">Confirmed</p>
            </div>
            <div class="text-center">
              <p class="text-yellow-500 text-4xl font-bold mb-2">{{ stats?.bookingsByStatus?.pending || 0 }}</p>
              <p class="text-gray-400">Pending</p>
            </div>
            <div class="text-center">
              <p class="text-red-500 text-4xl font-bold mb-2">{{ stats?.bookingsByStatus?.cancelled || 0 }}</p>
              <p class="text-gray-400">Cancelled</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    @keyframes shimmer {
      100% { transform: translateX(100%); }
    }
  `]
})
export class DashboardComponent implements OnInit {
  stats: any;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadStats();
  }

  loadStats() {
    this.http.get<any>(`${environment.apiUrl}/admin/dashboard/stats`).subscribe({
      next: (response) => {
        this.stats = response.data;
      },
      error: () => {
        // Use default values if API fails
        this.stats = {
          totalUsers: 3,
          totalMovies: 0,
          totalBookings: 0,
          totalRevenue: 0,
          bookingsByStatus: {
            confirmed: 0,
            pending: 0,
            cancelled: 0
          }
        };
      }
    });
  }

  navigateTo(path: string) {
    this.router.navigate([path]);
  }
}
