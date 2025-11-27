import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <nav class="fixed top-0 w-full z-50 transition-all duration-500 bg-black/80 backdrop-blur-xl py-3 border-b border-white/5 shadow-2xl" 
         [ngClass]="scrolled() ? 'shadow-2xl' : ''">
      <div class="container mx-auto px-4 md:px-8 flex justify-between items-center">
        
        <!-- Logo -->
        <div class="flex items-center gap-3 cursor-pointer group" routerLink="/">
           <div class="relative w-10 h-10 rounded-lg overflow-hidden flex items-center justify-center bg-gradient-to-br from-red-700 to-red-500 shadow-[0_0_15px_rgba(220,38,38,0.5)] group-hover:shadow-[0_0_25px_rgba(220,38,38,0.8)] transition-all duration-300">
              <span class="relative z-10 font-black text-xl italic">R</span>
              <div class="absolute inset-0 bg-white/20 opacity-0 group-hover:opacity-100 transition-opacity"></div>
           </div>
           <span class="text-2xl font-bold tracking-tighter group-hover:tracking-normal transition-all duration-300">Rev<span class="text-red-500">Tickets</span></span>
        </div>
        
        <!-- Desktop Menu -->
        <div class="hidden md:flex items-center bg-white/5 rounded-full px-6 py-2 backdrop-blur-md border border-white/5 shadow-inner gap-1">
          <a routerLink="/" routerLinkActive="text-white bg-white/10" [routerLinkActiveOptions]="{exact: true}" class="px-4 py-1.5 text-sm font-medium text-gray-400 hover:text-white hover:bg-white/10 rounded-full transition-all duration-300">Home</a>
          <a routerLink="/movies" routerLinkActive="text-white bg-white/10" class="px-4 py-1.5 text-sm font-medium text-gray-400 hover:text-white hover:bg-white/10 rounded-full transition-all duration-300">Movies</a>
          <a routerLink="/events" routerLinkActive="text-white bg-white/10" class="px-4 py-1.5 text-sm font-medium text-gray-400 hover:text-white hover:bg-white/10 rounded-full transition-all duration-300">Events</a>
        </div>

        <!-- Auth Button / User Menu -->
        <div *ngIf="!isLoggedIn">
          <a [routerLink]="['/auth/login']" [queryParams]="{returnUrl: router.url}" class="group relative px-6 py-2.5 rounded-full overflow-hidden bg-red-600 text-white font-semibold shadow-lg shadow-red-900/20 hover:shadow-red-600/40 transition-all duration-300 transform hover:-translate-y-0.5">
            <div class="absolute inset-0 w-full h-full bg-gradient-to-r from-transparent via-white/20 to-transparent -translate-x-[100%] group-hover:animate-shimmer"></div>
            <span class="relative flex items-center gap-2">
              Login <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path></svg>
            </span>
          </a>
        </div>
        <div *ngIf="isLoggedIn" class="relative">
          <button (click)="toggleUserMenu()" class="flex items-center gap-2 px-4 py-2 rounded-full bg-white/10 hover:bg-white/20 border border-white/20 transition-all">
            <div class="w-8 h-8 rounded-full bg-red-600 flex items-center justify-center text-white font-bold">
              {{ getUserInitial() }}
            </div>
            <svg class="w-4 h-4 transition-transform" [class.rotate-180]="showUserMenu()" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
            </svg>
          </button>
          
          <!-- Dropdown Menu -->
          <div *ngIf="showUserMenu()" class="absolute right-0 mt-2 w-56 bg-[#1a1a1a] rounded-2xl shadow-2xl border border-white/10 overflow-hidden backdrop-blur-xl z-50">
            <div class="px-4 py-3 border-b border-white/10">
              <p class="text-sm text-gray-400">Signed in as</p>
              <p class="font-semibold text-white truncate">{{ currentUser?.email }}</p>
            </div>
            <div class="py-2">
              <a routerLink="/profile" (click)="closeUserMenu()" class="flex items-center gap-3 px-4 py-3 hover:bg-white/5 transition-colors">
                <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                </svg>
                <span>My Account</span>
              </a>
              <a routerLink="/bookings/my-bookings" (click)="closeUserMenu()" class="flex items-center gap-3 px-4 py-3 hover:bg-white/5 transition-colors">
                <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z"></path>
                </svg>
                <span>My Bookings</span>
              </a>
            </div>
            <div class="border-t border-white/10">
              <button (click)="logout()" class="flex items-center gap-3 px-4 py-3 w-full hover:bg-red-500/10 text-red-400 hover:text-red-300 transition-colors">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
                </svg>
                <span>Logout</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </nav>
  `,
  styles: [`
    @keyframes shimmer {
      100% {
        transform: translateX(100%);
      }
    }
  `]
})
export class NavbarComponent implements OnInit, OnDestroy {
  scrolled = signal(false);
  showUserMenu = signal(false);
  isLoggedIn = false;
  currentUser: any = null;

  constructor(
    public router: Router,
    private authService: AuthService
  ) {
    if (typeof window !== 'undefined') {
      window.addEventListener('scroll', () => {
        this.scrolled.set(window.scrollY > 50);
      });
    }
  }

  ngOnInit() {
    // Subscribe to auth state changes for reactive UI updates
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.isLoggedIn = !!user;
      this.showUserMenu.set(false); // Close menu on auth state change
    });
  }

  toggleUserMenu() {
    this.showUserMenu.update(show => !show);
  }

  closeUserMenu() {
    this.showUserMenu.set(false);
  }

  getUserInitial(): string {
    return this.currentUser?.name?.charAt(0).toUpperCase() || 
           this.currentUser?.email?.charAt(0).toUpperCase() || 'U';
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  ngOnDestroy() {
    // Cleanup handled by async pipe equivalent in subscription
  }
}
