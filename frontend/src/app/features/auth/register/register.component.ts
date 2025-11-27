import { Component, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="relative min-h-screen w-full flex items-center justify-center px-4 py-12">
      <!-- Video Background -->
      <div class="absolute inset-0 w-full h-full overflow-hidden">
        <div class="absolute inset-0 bg-black/30 z-10"></div>
        <video #videoElement class="absolute inset-0 min-w-full min-h-full object-cover w-auto h-auto" autoplay loop muted playsinline>
          <source src="/assets/videos/VID_20251125002155.mp4" type="video/mp4" />
        </video>
      </div>

      <!-- Form Container -->
      <div class="relative z-20 w-full max-w-md animate-fadeIn">
        <div class="p-8 rounded-2xl backdrop-blur-sm bg-black/50 border border-white/10">
          
          <!-- Header -->
          <div class="mb-8 text-center">
            <h2 class="text-3xl font-bold mb-2 relative group">
              <span class="absolute -inset-1 bg-gradient-to-r from-purple-600/30 via-pink-500/30 to-blue-500/30 blur-xl opacity-75 group-hover:opacity-100 transition-all duration-500 animate-pulse"></span>
              <span class="relative inline-block text-white">RevTickets</span>
            </h2>
            <p class="text-white/80">Create Account</p>
          </div>

          <form [formGroup]="registerForm" (ngSubmit)="onSubmit()" class="space-y-6">
            <!-- Error Message -->
            <div *ngIf="error" class="bg-red-500/10 border border-red-500/50 text-red-400 px-4 py-3 rounded-xl animate-shake">
              {{ error }}
            </div>

            <!-- Full Name -->
            <div class="relative">
              <div class="absolute left-3 top-1/2 -translate-y-1/2">
                <svg class="w-[18px] h-[18px] text-white/60" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                </svg>
              </div>
              <input type="text" formControlName="fullName" placeholder="Full Name" required
                class="w-full pl-10 pr-3 py-2 bg-white/5 border border-white/10 rounded-lg text-white placeholder-white/60 focus:outline-none focus:border-purple-500/50 transition-colors" />
              <div *ngIf="registerForm.get('fullName')?.invalid && registerForm.get('fullName')?.touched" 
                   class="text-red-400 text-sm mt-1.5">Full name required</div>
            </div>

            <!-- Email -->
            <div class="relative">
              <div class="absolute left-3 top-1/2 -translate-y-1/2">
                <svg class="w-[18px] h-[18px] text-white/60" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
                </svg>
              </div>
              <input type="email" formControlName="email" placeholder="Email address" required
                class="w-full pl-10 pr-3 py-2 bg-white/5 border border-white/10 rounded-lg text-white placeholder-white/60 focus:outline-none focus:border-purple-500/50 transition-colors" />
              <div *ngIf="registerForm.get('email')?.invalid && registerForm.get('email')?.touched" 
                   class="text-red-400 text-sm mt-1.5">Valid email required</div>
            </div>

            <!-- Phone -->
            <div class="relative">
              <div class="absolute left-3 top-1/2 -translate-y-1/2">
                <svg class="w-[18px] h-[18px] text-white/60" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"></path>
                </svg>
              </div>
              <input type="tel" formControlName="phone" placeholder="Phone number" required
                class="w-full pl-10 pr-3 py-2 bg-white/5 border border-white/10 rounded-lg text-white placeholder-white/60 focus:outline-none focus:border-purple-500/50 transition-colors" />
              <div *ngIf="registerForm.get('phone')?.invalid && registerForm.get('phone')?.touched" 
                   class="text-red-400 text-sm mt-1.5">10-digit phone required</div>
            </div>

            <!-- Password -->
            <div class="relative">
              <div class="absolute left-3 top-1/2 -translate-y-1/2">
                <svg class="w-[18px] h-[18px] text-white/60" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                </svg>
              </div>
              <input [type]="showPassword ? 'text' : 'password'" formControlName="password" placeholder="Password" required
                class="w-full pl-10 pr-10 py-2 bg-white/5 border border-white/10 rounded-lg text-white placeholder-white/60 focus:outline-none focus:border-purple-500/50 transition-colors" />
              <button type="button" class="absolute right-3 top-1/2 -translate-y-1/2 text-white/60 hover:text-white transition-colors" 
                (click)="showPassword = !showPassword">
                <svg *ngIf="!showPassword" class="w-[18px] h-[18px]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
                </svg>
                <svg *ngIf="showPassword" class="w-[18px] h-[18px]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"></path>
                </svg>
              </button>
              <div *ngIf="registerForm.get('password')?.invalid && registerForm.get('password')?.touched" 
                   class="text-red-400 text-sm mt-1.5">Min 6 characters</div>
            </div>

            <!-- Submit Button -->
            <button type="submit" [disabled]="registerForm.invalid || loading"
              class="w-full py-3 rounded-lg bg-red-600 hover:bg-red-700 text-white font-medium transition-all duration-200 transform hover:-translate-y-1 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-opacity-50 disabled:opacity-70 disabled:cursor-not-allowed disabled:transform-none shadow-lg shadow-red-500/20 hover:shadow-red-500/40">
              {{ loading ? 'Creating Account...' : 'Sign Up' }}
            </button>
          </form>

          <!-- Sign In Link -->
          <p class="mt-8 text-center text-sm text-white/60">
            Already have an account? 
            <a routerLink="/auth/login" class="font-medium text-white hover:text-purple-300 transition-colors">Sign In</a>
          </p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    @keyframes fadeIn { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
    .animate-fadeIn { animation: fadeIn 0.6s ease-out; }
    @keyframes shake { 0%, 100% { transform: translateX(0); } 25% { transform: translateX(-10px); } 75% { transform: translateX(10px); } }
    .animate-shake { animation: shake 0.3s ease-in-out; }
  `]
})
export class RegisterComponent implements AfterViewInit {
  @ViewChild('videoElement') videoElement!: ElementRef<HTMLVideoElement>;
  registerForm: FormGroup;
  loading = false;
  error = '';
  showPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngAfterViewInit() {
    // Ensure video plays
    if (this.videoElement?.nativeElement) {
      this.videoElement.nativeElement.play().catch(err => {
        console.error('Video autoplay failed:', err);
      });
    }
  }

  onSubmit() {
    if (this.registerForm.invalid) {
      Object.keys(this.registerForm.controls).forEach(key => {
        this.registerForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.loading = true;
    this.error = '';

    this.authService.register(this.registerForm.value).subscribe({
      next: (response) => {
        this.loading = false;
        if (response.success) {
          // Don't save the token or user - registration should not auto-login
          // Clear any auth data that might have been saved
          this.authService.logout();
          // Redirect to login with success message
          this.router.navigate(['/auth/login'], { 
            state: { message: 'Registration successful! Please login to continue.' }
          });
        } else {
          this.error = response.message || 'Registration failed. Please try again.';
        }
      },
      error: (err) => {
        this.loading = false;
        console.error('Registration error:', err);
        this.error = err.error?.message || 'Registration failed. Please try again.';
      }
    });
  }
}
