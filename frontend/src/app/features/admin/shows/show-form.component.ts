import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-show-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  template: `
    <div class="min-h-screen bg-[#0a0f1e] p-8">
      <div class="max-w-4xl mx-auto">
        <button (click)="goBack()" class="flex items-center gap-2 text-gray-400 hover:text-white mb-4 transition-colors">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
          </svg>
          Back to Shows
        </button>
        
        <h1 class="text-4xl font-bold mb-8">{{isEditMode ? 'Edit' : 'Add New'}} Show</h1>
        
        <form [formGroup]="showForm" (ngSubmit)="onSubmit()" class="space-y-6">
          <!-- Movie/Event Selection -->
          <div class="bg-[#141b2d] rounded-2xl border border-white/10 p-8">
            <h2 class="text-2xl font-bold mb-6">Content Selection</h2>
            
            <div class="space-y-6">
              <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">Show Type</label>
                <select formControlName="showType" (change)="onShowTypeChange()" class="w-full px-4 py-3 bg-[#1a1a2e] border border-white/10 rounded-xl text-white focus:outline-none focus:border-red-500">
                  <option [ngValue]="null" class="bg-[#1a1a2e] text-white">Select Type</option>
                  <option value="MOVIE" class="bg-[#1a1a2e] text-white">Movie</option>
                  <option value="EVENT" class="bg-[#1a1a2e] text-white">Event</option>
                </select>
              </div>

              <div *ngIf="showForm.value.showType === 'MOVIE'">
                <label class="block text-sm font-medium text-gray-300 mb-2">Select Movie</label>
                <select formControlName="movieId" class="w-full px-4 py-3 bg-[#1a1a2e] border border-white/10 rounded-xl text-white focus:outline-none focus:border-red-500">
                  <option value="" class="bg-[#1a1a2e] text-white">Choose a movie</option>
                  <option *ngFor="let movie of movies" [value]="movie.movieId" class="bg-[#1a1a2e] text-white">
                    {{movie.title}} ({{movie.releaseDate | date:'yyyy'}})
                  </option>
                </select>
                <div class="text-xs text-gray-500 mt-1">Selected: {{showForm.get('movieId')?.value || 'None'}} | Movies: {{movies.length}}</div>
              </div>

              <div *ngIf="showForm.value.showType === 'EVENT'">
                <label class="block text-sm font-medium text-gray-300 mb-2">Select Event</label>
                <select formControlName="eventId" class="w-full px-4 py-3 bg-[#1a1a2e] border border-white/10 rounded-xl text-white focus:outline-none focus:border-red-500">
                  <option value="" class="bg-[#1a1a2e] text-white">Choose an event</option>
                  <option *ngFor="let event of events" [value]="event.eventId" class="bg-[#1a1a2e] text-white">{{event.title}}</option>
                </select>
                <div class="text-xs text-gray-500 mt-1">Selected: {{showForm.get('eventId')?.value || 'None'}} | Events: {{events.length}}</div>
              </div>
            </div>
          </div>

          <!-- Venue & Screen Selection -->
          <div class="bg-[#141b2d] rounded-2xl border border-white/10 p-8">
            <h2 class="text-2xl font-bold mb-6">Venue & Screen</h2>
            
            <div class="space-y-6">
              <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">Select Venue</label>
                <select formControlName="venueId" (change)="onVenueChange()" class="w-full px-4 py-3 bg-[#1a1a2e] border border-white/10 rounded-xl text-white focus:outline-none focus:border-red-500">
                  <option value="" class="bg-[#1a1a2e] text-white">Choose a venue</option>
                  <option value="OPEN_GROUND" class="bg-[#1a1a2e] text-white">🌟 Open Ground Event (Custom Zones)</option>
                  <option *ngFor="let venue of venues" [value]="venue.id" class="bg-[#1a1a2e] text-white">{{venue.name}} - {{venue.city}}</option>
                </select>
              </div>

              <div *ngIf="!isOpenGroundSelected">
                <label class="block text-sm font-medium text-gray-300 mb-2">Select Screen</label>
                <select formControlName="screenId" (change)="onScreenChange()" class="w-full px-4 py-3 bg-[#1a1a2e] border border-white/10 rounded-xl text-white focus:outline-none focus:border-red-500">
                  <option value="" class="bg-[#1a1a2e] text-white">Choose a screen</option>
                  <option *ngFor="let screen of selectedVenue?.screens" [value]="screen.id" class="bg-[#1a1a2e] text-white">
                    Screen {{screen.screenNumber}} - {{screen.screenType}} ({{screen.seatLayout?.totalSeats || 0}} seats)
                  </option>
                </select>
              </div>

              <!-- Open Ground Pricing Zones -->
              <div *ngIf="isOpenGroundSelected" class="bg-black/30 rounded-xl p-6 border border-white/10 mt-6">
                <h3 class="text-lg font-semibold mb-4 flex items-center gap-2">
                  <svg class="w-5 h-5 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z"/>
                  </svg>
                  Pricing Zones Configuration
                </h3>
                <p class="text-sm text-gray-400 mb-6">Define different pricing zones for your open ground event. Each zone will have its own price.</p>
                
                <!-- Pricing Zones -->
                <div class="space-y-4">
                  <div *ngFor="let zone of pricingZones; let i = index" class="flex items-center gap-4 bg-white/5 rounded-lg p-4 border border-white/10">
                    <div class="flex-1 grid grid-cols-3 gap-4">
                      <div>
                        <label class="block text-xs text-gray-400 mb-1">Zone Name</label>
                        <input [(ngModel)]="zone.name" [ngModelOptions]="{standalone: true}" type="text" class="w-full px-3 py-2 bg-black/30 border border-white/10 rounded-lg text-white text-sm focus:outline-none focus:border-red-500" placeholder="e.g., VIP, Gold, Silver">
                      </div>
                      <div>
                        <label class="block text-xs text-gray-400 mb-1">Price (₹)</label>
                        <input [(ngModel)]="zone.price" [ngModelOptions]="{standalone: true}" type="number" min="0" step="0.01" class="w-full px-3 py-2 bg-black/30 border border-white/10 rounded-lg text-white text-sm focus:outline-none focus:border-red-500" placeholder="500">
                      </div>
                      <div>
                        <label class="block text-xs text-gray-400 mb-1">Capacity</label>
                        <input [(ngModel)]="zone.capacity" [ngModelOptions]="{standalone: true}" type="number" min="1" class="w-full px-3 py-2 bg-black/30 border border-white/10 rounded-lg text-white text-sm focus:outline-none focus:border-red-500" placeholder="100">
                      </div>
                    </div>
                    <button type="button" (click)="removeZone(i)" class="p-2 bg-red-600/20 hover:bg-red-600/30 text-red-500 rounded-lg transition-colors">
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                      </svg>
                    </button>
                  </div>
                </div>
                
                <!-- Add Zone Button -->
                <button type="button" (click)="addZone()" class="mt-4 w-full px-4 py-3 bg-white/5 hover:bg-white/10 border-2 border-dashed border-white/20 hover:border-red-500/50 rounded-xl text-white transition-all flex items-center justify-center gap-2">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
                  </svg>
                  Add Pricing Zone
                </button>
                
                <!-- Zone Summary -->
                <div *ngIf="pricingZones.length > 0" class="mt-6 p-4 bg-blue-600/10 border border-blue-500/30 rounded-lg">
                  <div class="text-sm text-blue-300 font-semibold mb-2">Zone Summary:</div>
                  <div class="grid grid-cols-2 md:grid-cols-4 gap-3 text-xs">
                    <div>
                      <span class="text-gray-400">Total Zones:</span>
                      <span class="text-white ml-2 font-semibold">{{pricingZones.length}}</span>
                    </div>
                    <div>
                      <span class="text-gray-400">Total Capacity:</span>
                      <span class="text-white ml-2 font-semibold">{{getTotalCapacity()}}</span>
                    </div>
                    <div>
                      <span class="text-gray-400">Price Range:</span>
                      <span class="text-white ml-2 font-semibold">₹{{getMinPrice()}} - ₹{{getMaxPrice()}}</span>
                    </div>
                    <div>
                      <span class="text-gray-400">Avg Price:</span>
                      <span class="text-white ml-2 font-semibold">₹{{getAvgPrice()}}</span>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- Seat Layout Preview -->
              <div *ngIf="selectedScreen" class="bg-black/30 rounded-lg p-6">
                <h3 class="text-lg font-semibold mb-4">Screen {{selectedScreen.screenNumber}} - Seat Layout Preview</h3>
                <div class="text-sm text-gray-400 mb-4">
                  <p>Type: {{selectedScreen.screenType}} | Sound: {{selectedScreen.soundSystem}}</p>
                  <p>Total Seats: {{selectedScreen.seatLayout?.totalSeats || 0}}</p>
                </div>
                
                <div class="text-center mb-4">
                  <div class="inline-block px-16 py-2 bg-gradient-to-b from-gray-700 to-gray-900 rounded-t-full border-x-2 border-t-2 border-gray-600">
                    <span class="text-xs text-gray-400">SCREEN</span>
                  </div>
                </div>
                
                <div class="space-y-1 max-w-3xl mx-auto">
                  <div *ngFor="let row of selectedScreen.seatLayout?.rows" class="flex items-center justify-center gap-1">
                    <span class="w-6 text-center text-xs text-gray-500 font-mono">{{row}}</span>
                    <div class="flex gap-1">
                      <div *ngFor="let i of getSeatsArray(row)" 
                           [class]="isSeatDisabled(row, i) ? 'w-6 h-6 bg-red-600/20 border border-red-500/50 rounded text-xs' : 'w-6 h-6 bg-white/10 border border-white/30 rounded text-xs'">
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Show Schedule -->
          <div class="bg-[#141b2d] rounded-2xl border border-white/10 p-8">
            <h2 class="text-2xl font-bold mb-6">Schedule</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">Show Date</label>
                <input formControlName="showDate" type="date" 
                       [min]="minDate" 
                       [max]="maxDate"
                       class="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-xl text-white focus:outline-none focus:border-red-500">
                <p class="text-xs text-gray-400 mt-1" *ngIf="showForm.value.showType === 'MOVIE'">
                  Movies can only be scheduled within the next 7 days
                </p>
                <p class="text-xs text-gray-400 mt-1" *ngIf="showForm.value.showType === 'EVENT'">
                  Events can be scheduled for any future date
                </p>
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">Show Time</label>
                <input formControlName="showTime" type="time" class="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-xl text-white focus:outline-none focus:border-red-500">
              </div>
            </div>
          </div>

          <!-- Pricing -->
          <div class="bg-[#141b2d] rounded-2xl border border-white/10 p-8" *ngIf="!isOpenGroundSelected">
            <h2 class="text-2xl font-bold mb-6">Pricing</h2>
            
            <div>
              <label class="block text-sm font-medium text-gray-300 mb-2">Base Price per Seat (₹)</label>
              <input formControlName="price" type="number" min="0" step="0.01" class="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-xl text-white focus:outline-none focus:border-red-500" placeholder="200">
            </div>
          </div>

          
          <!-- Submit Buttons -->
          <div class="flex gap-4">
            <button type="submit" [disabled]="showForm.invalid || submitting" 
                    class="px-8 py-4 bg-red-600 hover:bg-red-700 text-white rounded-xl font-bold transition-all disabled:opacity-50 disabled:cursor-not-allowed">
              {{submitting ? 'Saving...' : (isEditMode ? 'Update Show' : 'Create Show')}}
            </button>
            <button type="button" (click)="goBack()" class="px-8 py-4 bg-gray-600 hover:bg-gray-700 text-white rounded-xl font-bold transition-all">
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class ShowFormComponent implements OnInit {
  showForm: FormGroup;
  isEditMode = false;
  showId: number | null = null;
  submitting = false;
  
  movies: any[] = [];
  events: any[] = [];
  venues: any[] = [];
  selectedVenue: any = null;
  selectedScreen: any = null;
  
  pricingZones: Array<{name: string, price: number, capacity: number}> = [];

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.showForm = this.fb.group({
      showType: ['', Validators.required],
      movieId: [''],
      eventId: [''],
      venueId: ['', Validators.required],
      screenId: ['', Validators.required],
      showDate: ['', Validators.required],
      showTime: ['', Validators.required],
      price: [200, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit() {
    this.loadMovies();
    this.loadEvents();
    this.loadVenues();
    
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.showId = +id;
      this.loadShow();
    }
  }

  loadMovies() {
    this.http.get<any>(`${environment.apiUrl}/admin/movies`).subscribe({
      next: (response) => {
        const allMovies = response.data || [];
        this.movies = allMovies.filter((m: any) => m.isActive);
        console.log('Loaded movies:', this.movies);
      }
    });
  }

  loadEvents() {
    this.http.get<any>(`${environment.apiUrl}/admin/events`).subscribe({
      next: (response) => {
        const allEvents = response.data || [];
        this.events = allEvents.filter((e: any) => e.isActive);
      }
    });
  }

  loadVenues() {
    this.http.get<any>(`${environment.apiUrl}/admin/venues`).subscribe({
      next: (response) => this.venues = response.data || []
    });
  }

  onShowTypeChange() {
    const showType = this.showForm.value.showType;
    if (showType === 'MOVIE') {
      this.showForm.patchValue({ eventId: '' });
      this.showForm.get('movieId')?.setValidators([Validators.required]);
      this.showForm.get('eventId')?.clearValidators();
    } else if (showType === 'EVENT') {
      this.showForm.patchValue({ movieId: '' });
      this.showForm.get('eventId')?.setValidators([Validators.required]);
      this.showForm.get('movieId')?.clearValidators();
    } else {
      this.showForm.get('movieId')?.clearValidators();
      this.showForm.get('eventId')?.clearValidators();
    }
    this.showForm.get('movieId')?.updateValueAndValidity();
    this.showForm.get('eventId')?.updateValueAndValidity();
  }

  onVenueChange() {
    const venueId = this.showForm.value.venueId;
    
    if (venueId === 'OPEN_GROUND') {
      // Open ground selected
      this.selectedVenue = null;
      this.selectedScreen = null;
      this.showForm.patchValue({ screenId: '' });
      
      // Initialize pricing zones
      if (this.pricingZones.length === 0) {
        this.pricingZones = [
          { name: 'VIP', price: 1000, capacity: 50 },
          { name: 'Gold', price: 500, capacity: 100 },
          { name: 'Silver', price: 300, capacity: 200 }
        ];
      }
      
      // Clear screen validation for open ground
      this.showForm.get('screenId')?.clearValidators();
      this.showForm.get('screenId')?.updateValueAndValidity();
      
      // Clear price requirement for open ground
      this.showForm.get('price')?.clearValidators();
      this.showForm.get('price')?.updateValueAndValidity();
    } else {
      // Regular venue selected
      this.selectedVenue = this.venues.find(v => v.id == venueId);
      this.selectedScreen = null;
      this.showForm.patchValue({ screenId: '' });
      this.pricingZones = [];
      
      // Restore screen validation
      this.showForm.get('screenId')?.setValidators([Validators.required]);
      this.showForm.get('screenId')?.updateValueAndValidity();
      
      // Restore price validation
      this.showForm.get('price')?.setValidators([Validators.required, Validators.min(0)]);
      this.showForm.get('price')?.updateValueAndValidity();
    }
  }

  get isOpenGroundSelected(): boolean {
    return this.showForm.value.venueId === 'OPEN_GROUND';
  }
  
  get minDate(): string {
    // Today's date in YYYY-MM-DD format (without timezone conversion)
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
  
  get maxDate(): string {
    // For movies: Today + 7 days
    // For events: null (no restriction) - but we'll set a far future date
    const showType = this.showForm.value.showType;
    const today = new Date();
    
    if (showType === 'MOVIE') {
      const maxDate = new Date(today.getFullYear(), today.getMonth(), today.getDate() + 7);
      const year = maxDate.getFullYear();
      const month = String(maxDate.getMonth() + 1).padStart(2, '0');
      const day = String(maxDate.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    } else {
      // For events, set a far future date (1 year from now)
      const maxDate = new Date(today.getFullYear() + 1, today.getMonth(), today.getDate());
      const year = maxDate.getFullYear();
      const month = String(maxDate.getMonth() + 1).padStart(2, '0');
      const day = String(maxDate.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }
  }

  onScreenChange() {
    const screenId = this.showForm.value.screenId;
    
    if (screenId === 'OPEN_GROUND') {
      this.selectedScreen = null;
      // Initialize pricing zones for open ground
      if (this.pricingZones.length === 0) {
        this.pricingZones = [
          { name: 'VIP', price: 1000, capacity: 50 },
          { name: 'Gold', price: 500, capacity: 100 },
          { name: 'Silver', price: 300, capacity: 200 }
        ];
      }
      // Clear price requirement for open ground
      this.showForm.get('price')?.clearValidators();
      this.showForm.get('price')?.updateValueAndValidity();
    } else if (this.selectedVenue) {
      this.selectedScreen = this.selectedVenue.screens.find((s: any) => s.id == screenId);
      // Restore price validation for regular screens
      this.showForm.get('price')?.setValidators([Validators.required, Validators.min(0)]);
      this.showForm.get('price')?.updateValueAndValidity();
    }
  }

  getSeatsArray(row: string): number[] {
    if (!this.selectedScreen?.seatLayout?.seatsPerRow) return [];
    const count = this.selectedScreen.seatLayout.seatsPerRow[row] || 0;
    return Array(count).fill(0).map((_, i) => i + 1);
  }

  isSeatDisabled(row: string, seatNum: number): boolean {
    const disabledSeats = this.selectedScreen?.seatLayout?.disabledSeats || [];
    return disabledSeats.includes(`${row}${seatNum}`);
  }

  loadShow() {
    this.http.get<any>(`${environment.apiUrl}/admin/shows/${this.showId}`).subscribe({
      next: (response) => {
        const show = response.data;
        this.showForm.patchValue({
          showType: show.showType,
          movieId: show.movieId,
          eventId: show.eventId,
          venueId: show.venueId,
          screenId: show.screenId,
          showDate: show.showDate,
          showTime: show.showTime,
          price: show.basePrice || show.standardPrice || 200
        });
        
        setTimeout(() => {
          this.onVenueChange();
          setTimeout(() => this.onScreenChange(), 100);
        }, 100);
      }
    });
  }

  onSubmit() {
    if (this.showForm.invalid) return;

    console.log('Form values before submit:', this.showForm.value);
    
    this.submitting = true;
    const formData: any = {
      showType: this.showForm.value.showType,
      movieId: this.showForm.value.movieId && this.showForm.value.movieId !== '' ? Number(this.showForm.value.movieId) : null,
      eventId: this.showForm.value.eventId && this.showForm.value.eventId !== '' ? Number(this.showForm.value.eventId) : null,
      showDate: this.showForm.value.showDate,
      showTime: this.showForm.value.showTime
    };
    
    // Handle Open Ground vs Regular Venue
    if (this.isOpenGroundSelected) {
      formData.venueId = null;
      formData.screenId = null;
      formData.isOpenGround = true;
    } else {
      formData.venueId = Number(this.showForm.value.venueId);
      formData.screenId = Number(this.showForm.value.screenId);
      formData.isOpenGround = false;
    }
    
    // Handle pricing based on open ground vs regular show
    if (this.isOpenGroundSelected && this.pricingZones.length > 0) {
      // Send pricing zones as an array for open event shows
      formData.pricingZones = this.pricingZones;
      formData.standardPrice = this.pricingZones[0]?.price || 200; // Use first zone as base
    } else {
      formData.standardPrice = Number(this.showForm.value.price);
      formData.premiumPrice = Number(this.showForm.value.price);
      formData.vipPrice = Number(this.showForm.value.price);
    }

    console.log('Form data to send:', formData);

    const request = this.isEditMode
      ? this.http.put(`${environment.apiUrl}/admin/shows/${this.showId}`, formData)
      : this.http.post(`${environment.apiUrl}/admin/shows`, formData);

    request.subscribe({
      next: () => {
        this.submitting = false;
        this.router.navigate(['/admin/shows']);
      },
      error: (err) => {
        this.submitting = false;
        console.error('Error saving show:', err);
        console.error('Error response:', err.error);
        alert('Failed to save show. Please try again.');
      }
    });
  }

  addZone() {
    this.pricingZones.push({ name: '', price: 0, capacity: 0 });
  }

  removeZone(index: number) {
    this.pricingZones.splice(index, 1);
  }

  getTotalCapacity(): number {
    return this.pricingZones.reduce((sum, zone) => sum + (zone.capacity || 0), 0);
  }

  getMinPrice(): number {
    if (this.pricingZones.length === 0) return 0;
    return Math.min(...this.pricingZones.map(z => z.price || 0));
  }

  getMaxPrice(): number {
    if (this.pricingZones.length === 0) return 0;
    return Math.max(...this.pricingZones.map(z => z.price || 0));
  }

  getAvgPrice(): number {
    if (this.pricingZones.length === 0) return 0;
    const sum = this.pricingZones.reduce((acc, z) => acc + (z.price || 0), 0);
    return Math.round(sum / this.pricingZones.length);
  }

  goBack() {
    this.router.navigate(['/admin/shows']);
  }
}
