import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

interface SeatItem {
  type: 'seat' | 'spacer';
  seatNumber?: number;  // Sequential number from backend (1, 2, 3...)
  rowLabel?: string;    // Row letter (A, B, C...)
  available?: boolean;
  price?: number;
  seatType?: string;
  seatId?: number;
  isBlocked?: boolean;
}

interface SeatRow {
  row: string;
  items: SeatItem[];
}

@Component({
  selector: 'app-seat-selection',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-[#050505] text-white pt-20 pb-32">
      <div class="container mx-auto px-4" *ngIf="show">
        <!-- Back Button -->
        <button (click)="goBack()" class="flex items-center gap-2 text-gray-400 hover:text-white transition-colors mb-6">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
          </svg>
          Back
        </button>

        <!-- Main Card -->
        <div class="max-w-6xl mx-auto bg-gradient-to-b from-[#1a0a0a] via-[#0a0a0a] to-[#050505] rounded-3xl border border-red-900/30 shadow-2xl">
          
          <!-- Header -->
          <div class="py-8 px-6 border-b border-red-900/20">
            <div class="flex items-start justify-between mb-4">
              <div>
                <h1 class="text-3xl font-bold mb-2">{{ isOpenEvent ? 'Select your zone' : 'Select your seat' }}</h1>
                <div class="flex flex-wrap gap-3 text-sm text-gray-400">
                  <span>{{ show.event?.title || show.movie?.title }}</span>
                  <span>•</span>
                  <span *ngIf="!isOpenEvent">{{ show.venue?.venueName }}, {{ show.venue?.city }}</span>
                  <span *ngIf="isOpenEvent">Open Ground Event</span>
                  <span>•</span>
                  <span>{{ show.showDate | date:'EEE, dd MMM, yyyy' }}</span>
                  <span>•</span>
                  <span>{{ formatShowTime(show.showTime) }}</span>
                  <span *ngIf="!isOpenEvent">•</span>
                  <span *ngIf="!isOpenEvent">Screen {{ show.screen?.screenNumber }}</span>
                </div>
              </div>
              
              <!-- Zoom Controls (only for seat layout) -->
              <div *ngIf="!isOpenEvent" class="flex items-center gap-2 bg-black/40 rounded-lg p-1 border border-white/10">
                <button (click)="zoomOut()" class="p-2 hover:bg-white/10 rounded transition-colors">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0zM13 10H7"></path>
                  </svg>
                </button>
                <span class="px-3 py-2 text-sm font-medium">{{ (zoomLevel * 100).toFixed(0) }}%</span>
                <button (click)="zoomIn()" class="p-2 hover:bg-white/10 rounded transition-colors">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0zM10 7v3m0 0v3m0-3h3m-3 0H7"></path>
                  </svg>
                </button>
              </div>
            </div>
          </div>

          <!-- ZONE SELECTION FOR OPEN EVENTS -->
          <div *ngIf="isOpenEvent" class="py-12 px-6">
            <div class="max-w-4xl mx-auto">
              <div class="text-center mb-12">
                <div class="inline-block px-12 py-3 bg-gradient-to-b from-purple-600/30 to-transparent border-t-4 border-x-4 border-purple-500/60 rounded-t-3xl">
                  <span class="text-purple-300 font-bold text-lg">STAGE</span>
                </div>
              </div>

              <div class="space-y-6">
                <div *ngFor="let zone of pricingZones" class="bg-gradient-to-r from-gray-900 to-black rounded-2xl border border-red-900/30 p-6">
                  <div class="flex items-center justify-between mb-4">
                    <div>
                      <h3 class="text-2xl font-bold mb-1" [class.text-yellow-400]="zone.name === 'VIP'" [class.text-amber-400]="zone.name === 'Gold'" [class.text-gray-300]="zone.name !== 'VIP' && zone.name !== 'Gold'">
                        {{ zone.name }} Zone
                      </h3>
                      <p class="text-gray-400 text-sm">{{ zone.availableCapacity || zone.capacity }} seats available</p>
                    </div>
                    <div class="text-right">
                      <p class="text-3xl font-bold text-red-500">₹{{ zone.price }}</p>
                      <p class="text-xs text-gray-500">per ticket</p>
                    </div>
                  </div>

                  <div class="flex items-center gap-4">
                    <label class="text-gray-400 text-sm">Tickets:</label>
                    <div class="flex items-center gap-3 bg-black/40 rounded-lg p-1 border border-white/10">
                      <button (click)="decrementZoneTickets(zone)" [disabled]="getZoneTicketCount(zone.name) === 0" class="px-4 py-2 hover:bg-white/10 rounded transition-colors disabled:opacity-30">
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4"></path>
                        </svg>
                      </button>
                      <span class="w-12 text-center font-bold text-lg">{{ getZoneTicketCount(zone.name) }}</span>
                      <button (click)="incrementZoneTickets(zone)" [disabled]="getZoneTicketCount(zone.name) >= (zone.availableCapacity || zone.capacity) || getTotalTickets() >= 10" class="px-4 py-2 hover:bg-white/10 rounded transition-colors disabled:opacity-30">
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                        </svg>
                      </button>
                    </div>
                    <div *ngIf="getZoneTicketCount(zone.name) > 0" class="ml-auto">
                      <p class="text-sm text-gray-400">Subtotal:</p>
                      <p class="text-xl font-bold text-white">₹{{ getZoneTicketCount(zone.name) * zone.price }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- SEAT LAYOUT FOR REGULAR SHOWS -->
          <div *ngIf="!isOpenEvent" class="py-12 px-6">
            <div class="max-w-4xl mx-auto">
              <!-- Single Price Display -->
              <div class="text-center mb-4">
                <span class="inline-block px-6 py-2 bg-red-900/20 border border-red-800/50 text-red-400 text-lg font-semibold rounded-lg">₹{{ show.basePrice }} per seat</span>
              </div>
              
              <!-- Screen -->
              <div class="relative h-20 mb-12">
                <div class="absolute inset-x-0 top-0 h-16 bg-gradient-to-b from-red-900/40 via-red-700/20 to-transparent rounded-t-[100%] blur-2xl"></div>
                <div class="absolute inset-x-0 top-0 h-12 bg-gradient-to-b from-gray-400/30 via-gray-500/20 to-transparent rounded-t-[100%] border-t-2 border-x-2 border-red-800/60"></div>
                <div class="absolute inset-x-0 top-2 h-10 bg-gradient-to-b from-red-500/20 to-transparent rounded-t-[100%]"></div>
              </div>

              <!-- Seats -->
              <div #seatContainer class="overflow-auto max-h-[500px] px-4 py-6 cursor-grab select-none" 
                   style="scrollbar-width: none; -ms-overflow-style: none;"
                   (mousedown)="onMouseDown($event, seatContainer)"
                   (mousemove)="onMouseMove($event, seatContainer)"
                   (mouseup)="onMouseUp(seatContainer)"
                   (mouseleave)="onMouseLeave(seatContainer)">
                <style>
                  div::-webkit-scrollbar { display: none; }
                </style>
                <div class="inline-block min-w-max mx-auto" [style.transform]="'scale(' + zoomLevel + ')'">
                  <div *ngFor="let row of seats" class="flex items-center justify-center gap-2 mb-2">
                    <div class="w-6 text-gray-400 font-bold text-xs text-center">{{ row.row }}</div>
                    
                    <div class="flex gap-1">
                      <ng-container *ngFor="let item of row.items">
                        <div *ngIf="item.type === 'spacer'" class="w-7 h-7"></div>
                        <button *ngIf="item.type === 'seat'" (click)="toggleSeat(item.rowLabel!, item.seatNumber!)" [disabled]="!item.available" [class]="getSeatClass(item.rowLabel!, item.seatNumber!, item.available!)" class="w-7 h-7 rounded text-[10px] font-semibold flex items-center justify-center border transition-all">
                          {{ item.seatNumber }}
                        </button>
                      </ng-container>
                    </div>
                    
                    <div class="w-6 text-gray-400 font-bold text-xs text-center">{{ row.row }}</div>
                  </div>
                </div>
              </div>

              <!-- Legend -->
              <div class="flex justify-center gap-8 mt-8 pt-6 border-t border-red-900/20 text-sm">
                <div class="flex items-center gap-2">
                  <div class="w-7 h-7 bg-transparent border border-gray-600 rounded"></div>
                  <span class="text-gray-400">Available</span>
                </div>
                <div class="flex items-center gap-2">
                  <div class="w-7 h-7 bg-red-600 border border-red-500 rounded"></div>
                  <span class="text-gray-400">Selected</span>
                </div>
                <div class="flex items-center gap-2">
                  <div class="w-7 h-7 bg-gray-800 border border-gray-700 rounded opacity-50"></div>
                  <span class="text-gray-400">Booked</span>
                </div>
              </div>
            </div>
          </div>

        </div>

        <!-- Booking Summary -->
        <div *ngIf="(isOpenEvent && getTotalTickets() > 0) || (!isOpenEvent && selectedSeats.length > 0)" class="fixed bottom-0 left-0 right-0 bg-gradient-to-t from-black via-[#0a0a0a] to-transparent border-t border-red-900/30 p-6 z-20">
          <div class="container mx-auto flex items-center justify-between max-w-6xl">
            <div>
              <p class="text-sm text-gray-400 mb-1">{{ isOpenEvent ? getTotalTickets() + ' Ticket(s)' : selectedSeats.length + ' Seat(s)' }} Selected</p>
              <p *ngIf="!isOpenEvent" class="text-lg font-semibold text-white mb-1">{{ selectedSeats.join(', ') }}</p>
              <p *ngIf="!isOpenEvent" class="text-sm text-gray-400 mb-1">{{ selectedSeats.length }} × ₹{{ show.basePrice }} = ₹{{ getTotalPrice() }}</p>
              <p class="text-3xl font-bold text-red-500">₹{{ getTotalPrice() }}</p>
            </div>
            <button (click)="proceedToPayment()" class="px-10 py-4 bg-red-600 hover:bg-red-700 rounded-xl font-bold text-lg transition-all shadow-lg hover:shadow-red-600/50">
              Proceed to Payment
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class SeatSelectionComponent implements OnInit {
  showId!: number;
  show: any;
  isOpenEvent = false;
  
  // Seat layout for regular shows
  seats: SeatRow[] = [];
  selectedSeatsMap = new Map<string, {seatNumber: number, price: number, seatId: number}>();
  seatDataMap = new Map<string, any>();
  
  // Zone selection for open events
  pricingZones: any[] = [];
  zoneTickets = new Map<string, number>();
  
  zoomLevel = 1;
  
  // Drag-to-pan properties
  isDragging = false;
  startX = 0;
  startY = 0;
  scrollLeft = 0;
  scrollTop = 0;

  get selectedSeats(): string[] {
    return Array.from(this.selectedSeatsMap.keys());
  }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.isOpenEvent = this.route.snapshot.queryParamMap.get('isOpenEvent') === 'true';
    const openShowId = this.route.snapshot.queryParamMap.get('openShowId');
    
    if (this.isOpenEvent && openShowId) {
      this.showId = +openShowId;
      this.loadOpenEventShow();
    } else {
      this.showId = +this.route.snapshot.queryParamMap.get('showId')!;
      this.loadShow();
    }
  }

  loadShow() {
    this.http.get<any>(`${environment.apiUrl}/shows/${this.showId}`).subscribe({
      next: (response) => {
        this.show = response.data;
        this.loadSeats();
      }
    });
  }

  loadSeats() {
    this.http.get<any>(`${environment.apiUrl}/seats/show/${this.showId}`).subscribe({
      next: (response) => {
        const seatData = response.data || [];
        console.log('Loaded seats:', seatData);
        
        // Group seats by row
        const rowMap = new Map<string, any[]>();
        seatData.forEach((seat: any) => {
          if (!rowMap.has(seat.rowLabel)) {
            rowMap.set(seat.rowLabel, []);
          }
          rowMap.get(seat.rowLabel)!.push(seat);
          this.seatDataMap.set(`${seat.rowLabel}${seat.seatNumber}`, seat);
        });
        
        // Build seat rows
        this.seats = Array.from(rowMap.entries())
          .sort((a, b) => a[0].localeCompare(b[0]))
          .map(([rowLabel, seats]) => ({
            row: rowLabel,
            items: seats
              .sort((a, b) => a.seatNumber - b.seatNumber)
              .map(seat => ({
                type: 'seat' as const,
                seatNumber: seat.seatNumber,
                rowLabel: seat.rowLabel,
                available: seat.isAvailable,
                price: seat.price,
                seatType: seat.seatType,
                seatId: seat.seatId,
                isBlocked: seat.isBlocked
              }))
          }));
        
        console.log('Processed seats:', this.seats);
      },
      error: (err) => {
        console.error('Error loading seats:', err);
      }
    });
  }

  loadOpenEventShow() {
    this.http.get<any>(`${environment.apiUrl}/shows/open-event-shows/${this.showId}`).subscribe({
      next: (response) => {
        this.show = response.data;
        this.pricingZones = this.show.pricingZones || [];
        this.pricingZones.forEach(zone => {
          this.zoneTickets.set(zone.name, 0);
          zone.availableCapacity = zone.availableCapacity || zone.capacity;
        });
      }
    });
  }

  toggleSeat(rowLabel: string, seatNumber: number) {
    const seatKey = `${rowLabel}${seatNumber}`;
    const seatInfo = this.seatDataMap.get(seatKey);
    
    if (!seatInfo || !seatInfo.isAvailable || seatInfo.isBlocked) return;
    
    if (this.selectedSeatsMap.has(seatKey)) {
      this.selectedSeatsMap.delete(seatKey);
    } else {
      if (this.selectedSeatsMap.size < 10) {
        this.selectedSeatsMap.set(seatKey, {
          seatNumber: seatNumber,
          price: seatInfo.price,
          seatId: seatInfo.seatId
        });
      }
    }
  }

  getSeatClass(rowLabel: string, seatNumber: number, available: boolean): string {
    if (!available) {
      return 'bg-gray-800 border-gray-700 opacity-50 cursor-not-allowed';
    }
    const seatKey = `${rowLabel}${seatNumber}`;
    return this.selectedSeatsMap.has(seatKey)
      ? 'bg-red-600 border-red-500 hover:bg-red-700 cursor-pointer shadow-lg' 
      : 'bg-transparent border-gray-600 hover:border-red-400 hover:bg-red-900/20 cursor-pointer';
  }

  incrementZoneTickets(zone: any) {
    const current = this.zoneTickets.get(zone.name) || 0;
    if (current < zone.availableCapacity && this.getTotalTickets() < 10) {
      this.zoneTickets.set(zone.name, current + 1);
    }
  }

  decrementZoneTickets(zone: any) {
    const current = this.zoneTickets.get(zone.name) || 0;
    if (current > 0) {
      this.zoneTickets.set(zone.name, current - 1);
    }
  }

  getZoneTicketCount(zoneName: string): number {
    return this.zoneTickets.get(zoneName) || 0;
  }

  getTotalTickets(): number {
    return Array.from(this.zoneTickets.values()).reduce((sum, count) => sum + count, 0);
  }

  getTotalPrice(): number {
    if (this.isOpenEvent) {
      return this.pricingZones.reduce((total, zone) => {
        return total + (this.getZoneTicketCount(zone.name) * zone.price);
      }, 0);
    } else {
      // Calculate from actual seat prices
      let total = 0;
      this.selectedSeatsMap.forEach(seatInfo => {
        total += seatInfo.price;
      });
      return total;
    }
  }

  getSelectedSeatPrice(seatLabel: string): number {
    const seatInfo = this.selectedSeatsMap.get(seatLabel);
    return seatInfo ? seatInfo.price : 0;
  }

  formatShowTime(time: string): string {
    if (!time) return '';
    const [hours, minutes] = time.split(':');
    const hour = parseInt(hours);
    const ampm = hour >= 12 ? 'PM' : 'AM';
    const displayHour = hour > 12 ? hour - 12 : hour === 0 ? 12 : hour;
    return `${displayHour}:${minutes} ${ampm}`;
  }

  zoomIn() {
    if (this.zoomLevel < 1.5) this.zoomLevel += 0.1;
  }

  zoomOut() {
    if (this.zoomLevel > 0.5) this.zoomLevel -= 0.1;
  }

  goBack() {
    this.router.navigate(['/movies']);
  }

  onMouseDown(event: MouseEvent, container: HTMLElement) {
    this.isDragging = true;
    this.startX = event.pageX - container.offsetLeft;
    this.startY = event.pageY - container.offsetTop;
    this.scrollLeft = container.scrollLeft;
    this.scrollTop = container.scrollTop;
    container.style.cursor = 'grabbing';
  }

  onMouseMove(event: MouseEvent, container: HTMLElement) {
    if (!this.isDragging) return;
    event.preventDefault();
    const x = event.pageX - container.offsetLeft;
    const y = event.pageY - container.offsetTop;
    const walkX = (x - this.startX) * 1.5;
    const walkY = (y - this.startY) * 1.5;
    container.scrollLeft = this.scrollLeft - walkX;
    container.scrollTop = this.scrollTop - walkY;
  }

  onMouseUp(container: HTMLElement) {
    this.isDragging = false;
    container.style.cursor = 'grab';
  }

  onMouseLeave(container: HTMLElement) {
    if (this.isDragging) {
      this.isDragging = false;
      container.style.cursor = 'grab';
    }
  }

  proceedToPayment() {
    if (this.isOpenEvent) {
      const zoneBookings = this.pricingZones
        .filter(zone => this.getZoneTicketCount(zone.name) > 0)
        .map(zone => ({
          zoneName: zone.name,
          quantity: this.getZoneTicketCount(zone.name),
          pricePerTicket: zone.price
        }));

      this.router.navigate(['/bookings/payment'], {
        queryParams: {
          openShowId: this.showId,
          isOpenEvent: true,
          zoneBookings: JSON.stringify(zoneBookings),
          totalAmount: this.getTotalPrice()
        }
      });
    } else {
      this.router.navigate(['/bookings/payment'], {
        queryParams: {
          showId: this.showId,
          seatNumbers: JSON.stringify(this.selectedSeats),
          totalAmount: this.getTotalPrice()
        }
      });
    }
  }
}
