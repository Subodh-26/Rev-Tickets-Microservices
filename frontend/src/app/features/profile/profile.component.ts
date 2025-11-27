import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-dark">
      <div class="container py-8">
        <div class="max-w-2xl mx-auto">
          <div class="card">
            <h1 class="text-3xl font-bold mb-6">My Profile</h1>
            
            <div class="space-y-4" *ngIf="user">
              <div>
                <label class="text-gray-400 text-sm">Name</label>
                <p class="text-xl">{{ user.name }}</p>
              </div>
              
              <div>
                <label class="text-gray-400 text-sm">Email</label>
                <p class="text-xl">{{ user.email }}</p>
              </div>
              
              <div>
                <label class="text-gray-400 text-sm">Phone</label>
                <p class="text-xl">{{ user.phone }}</p>
              </div>
              
              <div>
                <label class="text-gray-400 text-sm">Role</label>
                <p class="text-xl">
                  <span class="badge badge-success">{{ user.role }}</span>
                </p>
              </div>

              <div class="flex gap-4 pt-4">
                <button (click)="goToBookings()" class="btn btn-primary">My Bookings</button>
                <button *ngIf="user.role === 'ADMIN'" (click)="goToAdmin()" class="btn btn-secondary">Admin Dashboard</button>
                <button (click)="logout()" class="btn btn-secondary ml-auto">Logout</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ProfileComponent implements OnInit {
  user: any;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.user = this.authService.getCurrentUser();
    
    // Redirect admin users directly to admin dashboard
    if (this.user?.role === 'ADMIN') {
      this.router.navigate(['/admin']);
    }
  }

  goToBookings() {
    this.router.navigate(['/bookings/my-bookings']);
  }

  goToAdmin() {
    this.router.navigate(['/admin']);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
