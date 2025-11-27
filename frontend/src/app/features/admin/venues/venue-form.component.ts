import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, FormArray, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

interface SeatLayout {
  rows: string[];
  seatsPerRow: { [key: string]: number };
  aisles: { afterRow?: number; afterSeat?: { [key: string]: number[] } };
  totalSeats: number;
}

@Component({
  selector: 'app-venue-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="min-h-screen bg-[#0a0f1e] p-8">
      <div class="max-w-6xl mx-auto">
        <button (click)="goBack()" class="flex items-center gap-2 text-gray-400 hover:text-white mb-4 transition-colors">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
          </svg>
          Back to Venues
        </button>
        
        <h1 class="text-4xl font-bold mb-8">{{isEditMode ? 'Edit' : 'Add New'}} Venue</h1>
        
        <form [formGroup]="venueForm" (ngSubmit)="onSubmit()" class="space-y-6">
          <!-- Venue Basic Info -->
          <div class="bg-[#141b2d] rounded-2xl border border-white/10 p-8">
            <h2 class="text-2xl font-bold mb-6">Venue Information</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">Venue Name</label>
                <input formControlName="name" type="text" class="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-xl text-white focus:outline-none focus:border-red-500" placeholder="PVR Cinemas">
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-300 mb-2">City</label>
                <input formControlName="city" type="text" class="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-xl text-white focus:outline-none focus:border-red-500" placeholder="Mumbai">
              </div>
              
              <div class="md:col-span-2">
                <label class="block text-sm font-medium text-gray-300 mb-2">Address</label>
                <input formControlName="address" type="text" class="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-xl text-white focus:outline-none focus:border-red-500" placeholder="123 Main Street">
              </div>
            </div>
          </div>

          <!-- Screens Configuration -->
          <div class="bg-[#141b2d] rounded-2xl border border-white/10 p-8">
            <div class="flex justify-between items-center mb-6">
              <h2 class="text-2xl font-bold">Screen Configurations</h2>
              <button type="button" (click)="addScreen()" class="px-4 py-2 bg-red-600 hover:bg-red-700 rounded-lg text-sm font-medium transition-all">
                + Add Screen
              </button>
            </div>

            <div formArrayName="screens" class="space-y-6">
              <div *ngFor="let screen of screens.controls; let i = index" [formGroupName]="i" 
                   class="bg-black/20 rounded-xl p-6 border border-white/5">
                <div class="flex justify-between items-center mb-4">
                  <h3 class="text-xl font-semibold">Screen {{i + 1}}</h3>
                  <button type="button" (click)="removeScreen(i)" class="text-red-500 hover:text-red-400">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                    </svg>
                  </button>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                  <div>
                    <label class="block text-sm text-gray-400 mb-2">Screen Number</label>
                    <input formControlName="screenNumber" type="number" min="1" class="w-full px-4 py-2 bg-white/5 border border-white/10 rounded-lg text-white focus:outline-none focus:border-red-500">
                  </div>
                  
                  <div>
                    <label class="block text-sm text-gray-400 mb-2">Screen Type</label>
                    <select formControlName="screenType" class="w-full px-4 py-2 bg-[#1a1a2e] border border-white/10 rounded-lg text-white focus:outline-none focus:border-red-500">
                      <option value="2D" class="bg-[#1a1a2e] text-white">2D</option>
                      <option value="3D" class="bg-[#1a1a2e] text-white">3D</option>
                      <option value="IMAX" class="bg-[#1a1a2e] text-white">IMAX</option>
                      <option value="4DX" class="bg-[#1a1a2e] text-white">4DX</option>
                    </select>
                  </div>
                  
                  <div>
                    <label class="block text-sm text-gray-400 mb-2">Sound System</label>
                    <select formControlName="soundSystem" class="w-full px-4 py-2 bg-[#1a1a2e] border border-white/10 rounded-lg text-white focus:outline-none focus:border-red-500">
                      <option value="Dolby Atmos" class="bg-[#1a1a2e] text-white">Dolby Atmos</option>
                      <option value="DTS" class="bg-[#1a1a2e] text-white">DTS</option>
                      <option value="Standard" class="bg-[#1a1a2e] text-white">Standard</option>
                    </select>
                  </div>
                </div>

                <!-- Seat Layout Designer -->
                <div class="mt-6">
                  <h4 class="text-lg font-semibold mb-4">Seat Layout Design</h4>
                  
                  <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
                    <div>
                      <label class="block text-sm text-gray-400 mb-2">Number of Rows</label>
                      <input type="number" min="1" max="26" [value]="getSeatLayout(i).rows.length" 
                             (change)="updateRows(i, $event)" 
                             class="w-full px-4 py-2 bg-white/5 border border-white/10 rounded-lg text-white focus:outline-none focus:border-red-500">
                    </div>
                    
                    <div>
                      <label class="block text-sm text-gray-400 mb-2">Seats Per Row</label>
                      <input type="number" min="1" [value]="getDefaultSeatsPerRow(i)" 
                             (change)="updateDefaultSeatsPerRow(i, $event)" 
                             class="w-full px-4 py-2 bg-white/5 border border-white/10 rounded-lg text-white focus:outline-none focus:border-red-500">
                    </div>
                    
                    <div class="col-span-2">
                      <label class="block text-sm text-gray-400 mb-2">Total Seats: <span class="text-red-500 font-bold">{{getSeatLayout(i).totalSeats}}</span></label>
                    </div>
                  </div>

                  <!-- Seat Layout Preview -->
                  <div class="bg-black/30 rounded-lg p-6">
                    <div class="text-center mb-6">
                      <div class="inline-block px-20 py-2 bg-gradient-to-b from-gray-700 to-gray-900 rounded-t-full border-x-2 border-t-2 border-gray-600">
                        <span class="text-xs text-gray-400">SCREEN SIDE</span>
                      </div>
                    </div>
                    
                    <div class="space-y-2 max-w-4xl mx-auto">
                      <div *ngFor="let row of getSeatLayout(i).rows" class="flex items-center justify-center gap-2">
                        <span class="w-8 text-center text-sm text-gray-500 font-mono">{{row}}</span>
                        <div class="flex gap-1">
                          <button *ngFor="let seat of getSeatsForRow(i, row); let seatIdx = index" 
                                  type="button"
                                  (click)="toggleSeat(i, row, seatIdx)"
                                  [class]="getSeatClass(i, row, seatIdx)"
                                  class="w-8 h-8 rounded-md text-xs font-mono transition-all">
                            {{row}}{{seatIdx + 1}}
                          </button>
                        </div>
                      </div>
                    </div>
                    
                    <div class="flex justify-center gap-6 mt-6 text-sm">
                      <div class="flex items-center gap-2">
                        <div class="w-6 h-6 bg-white/10 border border-white/30 rounded"></div>
                        <span class="text-gray-400">Available</span>
                      </div>
                      <div class="flex items-center gap-2">
                        <div class="w-6 h-6 bg-red-600/20 border border-red-500/50 rounded"></div>
                        <span class="text-gray-400">Disabled</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Submit Buttons -->
          <div class="flex gap-4">
            <button type="submit" [disabled]="venueForm.invalid || submitting" 
                    class="px-8 py-4 bg-red-600 hover:bg-red-700 text-white rounded-xl font-bold transition-all disabled:opacity-50 disabled:cursor-not-allowed">
              {{submitting ? 'Saving...' : (isEditMode ? 'Update Venue' : 'Create Venue')}}
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
export class VenueFormComponent implements OnInit {
  venueForm: FormGroup;
  isEditMode = false;
  venueId: number | null = null;
  submitting = false;
  seatLayouts: SeatLayout[] = [];
  disabledSeats: Map<number, Set<string>> = new Map();

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.venueForm = this.fb.group({
      name: ['', Validators.required],
      address: ['', Validators.required],
      city: ['', Validators.required],
      screens: this.fb.array([])
    });
  }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.venueId = +id;
      this.loadVenue();
    } else {
      this.addScreen();
    }
  }

  get screens() {
    return this.venueForm.get('screens') as FormArray;
  }

  createScreen(): FormGroup {
    const index = this.screens.length;
    this.seatLayouts[index] = {
      rows: ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'],
      seatsPerRow: {},
      aisles: {},
      totalSeats: 0
    };
    
    // Initialize seats per row
    this.seatLayouts[index].rows.forEach(row => {
      this.seatLayouts[index].seatsPerRow[row] = 10;
    });
    this.calculateTotalSeats(index);
    
    this.disabledSeats.set(index, new Set());

    return this.fb.group({
      screenNumber: [this.screens.length + 1, Validators.required],
      screenType: ['2D', Validators.required],
      soundSystem: ['Dolby Atmos', Validators.required],
      seatLayout: [this.seatLayouts[index], Validators.required]
    });
  }

  addScreen() {
    this.screens.push(this.createScreen());
  }

  removeScreen(index: number) {
    this.screens.removeAt(index);
    this.seatLayouts.splice(index, 1);
    this.disabledSeats.delete(index);
  }

  getSeatLayout(index: number): SeatLayout {
    return this.seatLayouts[index] || { rows: [], seatsPerRow: {}, aisles: {}, totalSeats: 0 };
  }

  updateRows(screenIndex: number, event: any) {
    const numRows = parseInt(event.target.value);
    const alphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    const layout = this.seatLayouts[screenIndex];
    layout.rows = alphabet.substring(0, numRows).split('');
    
    // Ensure all rows have seat counts
    layout.rows.forEach(row => {
      if (!layout.seatsPerRow[row]) {
        layout.seatsPerRow[row] = 10;
      }
    });
    
    this.calculateTotalSeats(screenIndex);
    this.updateFormLayoutValue(screenIndex);
  }

  getDefaultSeatsPerRow(index: number): number {
    const layout = this.getSeatLayout(index);
    return layout.rows.length > 0 ? (layout.seatsPerRow[layout.rows[0]] || 10) : 10;
  }

  updateDefaultSeatsPerRow(screenIndex: number, event: any) {
    const seats = parseInt(event.target.value);
    const layout = this.seatLayouts[screenIndex];
    layout.rows.forEach(row => {
      layout.seatsPerRow[row] = seats;
    });
    this.calculateTotalSeats(screenIndex);
    this.updateFormLayoutValue(screenIndex);
  }

  getSeatsForRow(screenIndex: number, row: string): number[] {
    const count = this.getSeatLayout(screenIndex).seatsPerRow[row] || 0;
    return Array(count).fill(0).map((_, i) => i);
  }

  toggleSeat(screenIndex: number, row: string, seatIndex: number) {
    const seatId = `${row}${seatIndex + 1}`;
    const disabled = this.disabledSeats.get(screenIndex) || new Set();
    
    if (disabled.has(seatId)) {
      disabled.delete(seatId);
    } else {
      disabled.add(seatId);
    }
    
    this.disabledSeats.set(screenIndex, disabled);
    this.calculateTotalSeats(screenIndex);
    this.updateFormLayoutValue(screenIndex);
  }

  getSeatClass(screenIndex: number, row: string, seatIndex: number): string {
    const seatId = `${row}${seatIndex + 1}`;
    const disabled = this.disabledSeats.get(screenIndex) || new Set();
    
    return disabled.has(seatId)
      ? 'bg-red-600/20 border border-red-500/50 text-red-400 cursor-pointer hover:bg-red-600/30'
      : 'bg-white/10 border border-white/30 text-white hover:bg-white/20 cursor-pointer';
  }

  calculateTotalSeats(screenIndex: number) {
    const layout = this.seatLayouts[screenIndex];
    const disabled = this.disabledSeats.get(screenIndex) || new Set();
    
    let total = 0;
    layout.rows.forEach(row => {
      const seatsInRow = layout.seatsPerRow[row] || 0;
      for (let i = 1; i <= seatsInRow; i++) {
        if (!disabled.has(`${row}${i}`)) {
          total++;
        }
      }
    });
    
    layout.totalSeats = total;
  }

  updateFormLayoutValue(screenIndex: number) {
    const disabled = Array.from(this.disabledSeats.get(screenIndex) || new Set());
    const layoutWithDisabled = {
      ...this.seatLayouts[screenIndex],
      disabledSeats: disabled
    };
    this.screens.at(screenIndex).patchValue({ seatLayout: layoutWithDisabled });
  }

  loadVenue() {
    // Load existing venue data
    this.http.get<any>(`${environment.apiUrl}/admin/venues/${this.venueId}`).subscribe({
      next: (response) => {
        const venue = response.data;
        this.venueForm.patchValue({
          name: venue.name,
          address: venue.address,
          city: venue.city
        });
        
        // Load screens if available - reverse order so highest screen number appears first
        if (venue.screens && venue.screens.length > 0) {
          const reversedScreens = [...venue.screens].reverse();
          reversedScreens.forEach((screen: any) => {
            this.addScreen();
            const index = this.screens.length - 1;
            this.screens.at(index).patchValue(screen);
            if (screen.seatLayout) {
              this.seatLayouts[index] = screen.seatLayout;
              if (screen.seatLayout.disabledSeats) {
                this.disabledSeats.set(index, new Set(screen.seatLayout.disabledSeats));
              }
            }
          });
        }
      }
    });
  }

  onSubmit() {
    if (this.venueForm.invalid) return;

    this.submitting = true;
    const formData = {
      ...this.venueForm.value,
      totalScreens: this.screens.length, // Calculate from screens array
      screens: [...this.venueForm.value.screens].reverse() // Reverse back to correct order before sending
    };

    const request = this.isEditMode
      ? this.http.put(`${environment.apiUrl}/admin/venues/${this.venueId}`, formData)
      : this.http.post(`${environment.apiUrl}/admin/venues`, formData);

    request.subscribe({
      next: () => {
        this.submitting = false;
        this.router.navigate(['/admin/venues']);
      },
      error: (err) => {
        this.submitting = false;
        console.error('Error saving venue:', err);
        alert('Failed to save venue. Please try again.');
      }
    });
  }

  goBack() {
    this.router.navigate(['/admin/venues']);
  }
}
