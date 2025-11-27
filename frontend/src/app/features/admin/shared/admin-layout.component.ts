import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  template: `
    <div class="min-h-screen bg-[#0a0f1e] text-white flex">
      <!-- Sidebar -->
      <aside class="w-64 bg-[#141b2d] border-r border-white/10 fixed h-full overflow-y-auto">
        <!-- Logo -->
        <div class="p-6 border-b border-white/10">
          <a routerLink="/" class="flex items-center gap-3 hover:opacity-80 transition-opacity">
            <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-red-700 to-red-500 flex items-center justify-center shadow-lg shadow-red-900/50">
              <span class="font-black text-xl italic">R</span>
            </div>
            <span class="text-xl font-bold">Rev<span class="text-red-500">Tickets</span></span>
          </a>
        </div>

        <!-- Admin Panel Title -->
        <div class="px-6 py-4 border-b border-white/10">
          <h2 class="text-lg font-bold text-gray-400 uppercase tracking-wider">Admin Panel</h2>
        </div>

        <!-- Navigation -->
        <nav class="p-4 space-y-1">
          <a routerLink="/admin" [routerLinkActive]="'bg-red-600 text-white'" [routerLinkActiveOptions]="{exact: true}"
             class="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/5 transition-all group">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"></path>
            </svg>
            <span class="font-medium">Dashboard</span>
          </a>

          <a routerLink="/admin/movies" [routerLinkActive]="'bg-red-600 text-white'"
             class="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/5 transition-all group">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 4v16M17 4v16M3 8h4m10 0h4M3 12h18M3 16h4m10 0h4M4 20h16a1 1 0 001-1V5a1 1 0 00-1-1H4a1 1 0 00-1 1v14a1 1 0 001 1z"></path>
            </svg>
            <span class="font-medium">Movies</span>
          </a>

          <a routerLink="/admin/shows" [routerLinkActive]="'bg-red-600 text-white'"
             class="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/5 transition-all group">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
            <span class="font-medium">Shows</span>
          </a>

          <a routerLink="/admin/events" [routerLinkActive]="'bg-red-600 text-white'"
             class="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/5 transition-all group">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
            </svg>
            <span class="font-medium">Events</span>
          </a>

          <a routerLink="/admin/venues" [routerLinkActive]="'bg-red-600 text-white'"
             class="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/5 transition-all group">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
            </svg>
            <span class="font-medium">Venues</span>
          </a>

          <a routerLink="/admin/bookings" [routerLinkActive]="'bg-red-600 text-white'"
             class="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/5 transition-all group">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z"></path>
            </svg>
            <span class="font-medium">Bookings</span>
          </a>

          <a routerLink="/admin/users" [routerLinkActive]="'bg-red-600 text-white'"
             class="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/5 transition-all group">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path>
            </svg>
            <span class="font-medium">Users</span>
          </a>
        </nav>

        <!-- Logout Button -->
        <div class="p-4 border-t border-white/10 mt-auto">
          <button (click)="logout()" class="w-full flex items-center gap-3 px-4 py-3 rounded-xl bg-red-600/10 hover:bg-red-600 text-red-400 hover:text-white transition-all">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
            </svg>
            <span class="font-medium">Logout</span>
          </button>
        </div>
      </aside>

      <!-- Main Content -->
      <main class="flex-1 ml-64">
        <router-outlet></router-outlet>
      </main>
    </div>
  `
})
export class AdminLayoutComponent {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
