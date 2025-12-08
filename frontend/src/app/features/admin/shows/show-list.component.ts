import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-show-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="min-h-screen bg-[#0a0f1e] p-8">
      <div class="max-w-7xl mx-auto">
        <div class="flex justify-between items-center mb-8">
          <div>
            <h1 class="text-4xl font-bold mb-2">Manage Shows</h1>
            <p class="text-gray-400">Create and manage movie showtimes</p>
          </div>
          <button (click)="addShow()" class="px-6 py-3 bg-red-600 hover:bg-red-700 rounded-xl font-semibold transition-all transform hover:-translate-y-0.5 shadow-lg shadow-red-900/20">
            + Add New Show
          </button>
        </div>

        <div class="bg-[#141b2d] rounded-2xl border border-white/10 overflow-hidden">
          <div *ngIf="loading" class="p-12 text-center">
            <div class="inline-block animate-spin rounded-full h-12 w-12 border-4 border-red-600 border-t-transparent"></div>
            <p class="mt-4 text-gray-400">Loading shows...</p>
          </div>

          <div *ngIf="!loading && shows.length === 0" class="p-12 text-center">
            <p class="text-xl text-gray-400 mb-6">No shows found. Add your first show!</p>
            <button (click)="addShow()" class="px-6 py-3 bg-red-600 hover:bg-red-700 rounded-xl font-semibold transition-all">
              + Add Show
            </button>
          </div>

          <table *ngIf="!loading && shows.length > 0" class="w-full">
            <thead class="bg-[#1e293b] border-b border-white/10">
              <tr>
                <th class="px-6 py-4 text-left text-sm font-semibold text-gray-300">Movie</th>
                <th class="px-6 py-4 text-left text-sm font-semibold text-gray-300">Venue</th>
                <th class="px-6 py-4 text-left text-sm font-semibold text-gray-300">Screen</th>
                <th class="px-6 py-4 text-left text-sm font-semibold text-gray-300">Date & Time</th>
                <th class="px-6 py-4 text-left text-sm font-semibold text-gray-300">Price</th>
                <th class="px-6 py-4 text-left text-sm font-semibold text-gray-300">Available Seats</th>
                <th class="px-6 py-4 text-left text-sm font-semibold text-gray-300">Progress</th>
                <th class="px-6 py-4 text-left text-sm font-semibold text-gray-300">Status</th>
                <th class="px-6 py-4 text-right text-sm font-semibold text-gray-300">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-white/5">
              <tr *ngFor="let show of shows" class="hover:bg-white/5 transition-colors">
                <td class="px-6 py-4 font-semibold text-white">{{ show.movie?.title || show.event?.title }}</td>
                <td class="px-6 py-4 text-gray-300">{{ show.venue?.venueName }}, {{ show.venue?.city }}</td>
                <td class="px-6 py-4 text-gray-300">Screen {{ show.screen?.screenNumber }}</td>
                <td class="px-6 py-4">
                  <p class="text-white">{{ show.showDate | date:'mediumDate' }}</p>
                  <p class="text-sm text-gray-400">{{ show.showTime | date:'shortTime' }}</p>
                </td>
                <td class="px-6 py-4 text-gray-300">₹{{ show.basePrice }}</td>
                <td class="px-6 py-4">
                  <span class="text-white font-semibold">{{ show.availableSeats }}</span>
                  <span class="text-gray-400"> / {{ show.totalSeats }}</span>
                </td>
                <td class="px-6 py-4">
                  <div class="w-32 bg-gray-700 rounded-full h-2">
                    <div class="bg-green-500 h-2 rounded-full" [style.width.%]="((show.totalSeats - show.availableSeats) / show.totalSeats) * 100"></div>
                  </div>
                </td>
                <td class="px-6 py-4">
                  <span *ngIf="show.isActive" class="px-3 py-1 bg-green-600/20 text-green-400 rounded-full text-xs font-semibold">Active</span>
                  <span *ngIf="!show.isActive" class="px-3 py-1 bg-red-600/20 text-red-400 rounded-full text-xs font-semibold">Inactive</span>
                </td>
                <td class="px-6 py-4">
                  <div class="flex justify-end gap-2">
                    <button (click)="editShow(show.showId)" class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-sm font-medium transition-all">
                      Edit
                    </button>
                    <button *ngIf="show.isActive" (click)="deleteShow(show.showId)" class="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg text-sm font-medium transition-all">
                      Delete
                    </button>
                    <button *ngIf="!show.isActive" (click)="activateShow(show.showId)" class="px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg text-sm font-medium transition-all">
                      Activate
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class ShowListComponent implements OnInit {
  shows: any[] = [];
  loading = true;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    this.loadShows();
  }

  loadShows() {
    this.loading = true;
    this.shows = []; // Clear existing shows first
    this.http.get<any>(`${environment.apiUrl}/admin/shows`).subscribe({
      next: (response) => {
        console.log('Admin shows response:', response);
        const data = response.data || response;
        
        // Check if response has separate regular and open event shows
        if (data && typeof data === 'object' && data.regularShows !== undefined && data.openEventShows !== undefined) {
          const regularShows = data.regularShows || [];
          const openEventShows = data.openEventShows || [];
          console.log('Admin regular shows:', regularShows.length, regularShows);
          console.log('Admin open event shows:', openEventShows.length, openEventShows);
          
          // Convert regular show times from string to Date
          const mappedRegularShows = regularShows.map((show: any) => ({
            ...show,
            isActive: show.isActive !== undefined ? show.isActive : true,
            showTime: this.parseTime(show.showDate, show.showTime)
          }));
          
          // Map open event shows to match regular show structure for display
          const mappedOpenShows = openEventShows.map((openShow: any) => ({
            showId: `open-${openShow.openShowId}`,
            openShowId: openShow.openShowId,
            isOpenEventShow: true,
            isActive: openShow.isActive !== undefined ? openShow.isActive : true,
            movie: null,
            event: openShow.event,
            venue: { venueName: 'Open Ground Event', city: '' },
            screen: { screenNumber: 'N/A' },
            showDate: openShow.showDate,
            showTime: this.parseTime(openShow.showDate, openShow.showTime),
            basePrice: this.getMinPrice(openShow.pricingZones),
            availableSeats: openShow.availableCapacity,
            totalSeats: openShow.totalCapacity
          }));
          
          console.log('Mapped open shows:', mappedOpenShows);
          this.shows = [...mappedRegularShows, ...mappedOpenShows];
          console.log('Combined shows before sort:', this.shows.length, this.shows);
        } else {
          // Old structure - parse times
          this.shows = Array.isArray(data) ? data.map((show: any) => ({
            ...show,
            showTime: this.parseTime(show.showDate, show.showTime)
          })) : [];
        }
        
        // Sort by date descending
        this.shows.sort((a, b) => new Date(b.showDate).getTime() - new Date(a.showDate).getTime());
        
        console.log('Final shows array:', this.shows.length, this.shows);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.shows = [];
      }
    });
  }
  
  getMinPrice(pricingZones: any[]): number {
    if (!pricingZones || pricingZones.length === 0) return 0;
    return Math.min(...pricingZones.map((zone: any) => Number(zone.price)));
  }

  parseTime(dateStr: string, timeStr: string): Date {
    // Convert "2025-11-25" and "14:00:00" to a proper Date object
    const [hours, minutes] = timeStr.split(':');
    const date = new Date(dateStr);
    date.setHours(parseInt(hours), parseInt(minutes), 0, 0);
    return date;
  }

  addShow() {
    this.router.navigate(['/admin/shows/add']);
  }

  editShow(id: number) {
    this.router.navigate(['/admin/shows/edit', id]);
  }

  deleteShow(id: number | string) {
    if (confirm('Are you sure you want to delete this show?')) {
      // Check if it's an open event show
      const isOpenEventShow = typeof id === 'string' && id.toString().startsWith('open-');
      
      if (isOpenEventShow) {
        const openShowId = id.toString().replace('open-', '');
        this.http.put(`${environment.apiUrl}/shows/open-event-shows/${openShowId}/soft-delete`, {}).subscribe({
          next: () => this.loadShows(),
          error: (err) => alert('Failed to delete open event show: ' + err.message)
        });
      } else {
        this.http.put(`${environment.apiUrl}/shows/${id}/soft-delete`, {}).subscribe({
          next: () => this.loadShows(),
          error: (err) => alert('Failed to delete show: ' + err.message)
        });
      }
    }
  }

  activateShow(id: number | string) {
    if (confirm('Are you sure you want to activate this show?')) {
      // Check if it's an open event show
      const isOpenEventShow = typeof id === 'string' && id.toString().startsWith('open-');
      
      if (isOpenEventShow) {
        const openShowId = id.toString().replace('open-', '');
        this.http.put(`${environment.apiUrl}/shows/open-event-shows/${openShowId}/activate`, {}).subscribe({
          next: () => this.loadShows(),
          error: (err) => alert('Failed to activate open event show: ' + err.message)
        });
      } else {
        this.http.put(`${environment.apiUrl}/shows/${id}/activate`, {}).subscribe({
          next: () => this.loadShows(),
          error: (err) => alert('Failed to activate show: ' + err.message)
        });
      }
    }
  }
}
