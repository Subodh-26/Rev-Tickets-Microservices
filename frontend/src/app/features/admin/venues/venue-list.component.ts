import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-venue-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="min-h-screen bg-[#0a0f1e] p-8">
      <div class="max-w-7xl mx-auto">
        <div class="flex justify-between items-center mb-8">
          <div>
            <h1 class="text-4xl font-bold mb-2">Manage Venues</h1>
            <p class="text-gray-400">Create and manage cinema venues and screens</p>
          </div>
          <button (click)="addVenue()" class="px-6 py-3 bg-red-600 hover:bg-red-700 rounded-xl font-semibold transition-all transform hover:-translate-y-0.5 shadow-lg shadow-red-900/20">
            + Add New Venue
          </button>
        </div>

        <div class="bg-[#141b2d] rounded-2xl border border-white/10 overflow-hidden">
          <div *ngIf="loading" class="p-12 text-center">
            <div class="inline-block animate-spin rounded-full h-12 w-12 border-4 border-red-600 border-t-transparent"></div>
            <p class="mt-4 text-gray-400">Loading venues...</p>
          </div>

          <div *ngIf="!loading && venues.length === 0" class="p-12 text-center">
            <p class="text-xl text-gray-400 mb-6">No venues found. Add your first venue!</p>
            <button (click)="addVenue()" class="px-6 py-3 bg-red-600 hover:bg-red-700 rounded-xl font-semibold transition-all">
              + Add Venue
            </button>
          </div>

          <table *ngIf="!loading && venues.length > 0" class="w-full">
            <thead class="bg-[#1e293b] border-b border-white/10">
              <tr>
                <th class="px-6 py-4 text-left text-sm font-semibold text-gray-300">Venue Name</th>
                <th class="px-6 py-4 text-left text-sm font-semibold text-gray-300">Location</th>
                <th class="px-6 py-4 text-left text-sm font-semibold text-gray-300">City</th>
                <th class="px-6 py-4 text-left text-sm font-semibold text-gray-300">Screens</th>
                <th class="px-6 py-4 text-left text-sm font-semibold text-gray-300">Total Seats</th>
                <th class="px-6 py-4 text-right text-sm font-semibold text-gray-300">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-white/5">
              <tr *ngFor="let venue of venues" class="hover:bg-white/5 transition-colors">
                <td class="px-6 py-4 font-semibold text-white">{{ venue.name }}</td>
                <td class="px-6 py-4 text-gray-300">{{ venue.address }}</td>
                <td class="px-6 py-4 text-gray-300">{{ venue.city }}</td>
                <td class="px-6 py-4 text-gray-300">{{ venue.screenCount }}</td>
                <td class="px-6 py-4 text-gray-300">{{ venue.totalSeats }}</td>
                <td class="px-6 py-4">
                  <div class="flex justify-end gap-2">
                    <button (click)="editVenue(venue.id)" class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-sm font-medium transition-all">
                      Edit
                    </button>
                    <button (click)="deleteVenue(venue.id)" class="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg text-sm font-medium transition-all">
                      Delete
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
export class VenueListComponent implements OnInit {
  venues: any[] = [];
  loading = true;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    this.loadVenues();
  }

  loadVenues() {
    this.loading = true;
    this.http.get<any>(`${environment.apiUrl}/admin/venues`).subscribe({
      next: (response) => {
        this.venues = response.data || response;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.venues = [];
      }
    });
  }

  addVenue() {
    this.router.navigate(['/admin/venues/add']);
  }

  editVenue(id: number) {
    this.router.navigate(['/admin/venues/edit', id]);
  }

  deleteVenue(id: number) {
    if (confirm('Are you sure you want to delete this venue?')) {
      this.http.delete(`${environment.apiUrl}/admin/venues/${id}`).subscribe({
        next: () => this.loadVenues()
      });
    }
  }
}
