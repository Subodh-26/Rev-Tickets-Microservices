import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-[#0a0f1e] p-8">
      <div class="max-w-7xl mx-auto">
        <div class="mb-8">
          <h1 class="text-4xl font-bold mb-2">All Users</h1>
          <p class="text-gray-400">Manage platform users</p>
        </div>

        <!-- Users Table -->
        <div class="bg-[#121212] rounded-2xl border border-white/5 overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full">
              <thead class="bg-[#1a1a1a] border-b border-white/10">
                <tr>
                  <th class="px-6 py-4 text-left text-sm font-semibold text-gray-400">User ID</th>
                  <th class="px-6 py-4 text-left text-sm font-semibold text-gray-400">Full Name</th>
                  <th class="px-6 py-4 text-left text-sm font-semibold text-gray-400">Email</th>
                  <th class="px-6 py-4 text-left text-sm font-semibold text-gray-400">Phone</th>
                  <th class="px-6 py-4 text-left text-sm font-semibold text-gray-400">Role</th>
                  <th class="px-6 py-4 text-left text-sm font-semibold text-gray-400">Joined</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-white/5">
                @if (loading) {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-gray-400">
                      <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-red-600 border-t-transparent"></div>
                      <p class="mt-4">Loading users...</p>
                    </td>
                  </tr>
                } @else if (users.length === 0) {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-gray-400">
                      No users found
                    </td>
                  </tr>
                } @else {
                  @for (user of users; track user.userId) {
                    <tr class="hover:bg-white/5 transition-colors">
                      <td class="px-6 py-4 text-sm text-white">{{ user.userId }}</td>
                      <td class="px-6 py-4 text-sm text-white font-medium">{{ user.fullName }}</td>
                      <td class="px-6 py-4 text-sm text-gray-400">{{ user.email }}</td>
                      <td class="px-6 py-4 text-sm text-gray-400">{{ user.phone }}</td>
                      <td class="px-6 py-4">
                        <span [ngClass]="{
                          'bg-red-500/20 text-red-400 border-red-500/50': user.role === 'ADMIN',
                          'bg-blue-500/20 text-blue-400 border-blue-500/50': user.role === 'USER'
                        }" class="px-3 py-1 rounded-full text-xs font-semibold border">
                          {{ user.role }}
                        </span>
                      </td>
                      <td class="px-6 py-4 text-sm text-gray-400">{{ user.createdAt | date:'medium' }}</td>
                    </tr>
                  }
                }
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  `
})
export class UsersComponent implements OnInit {
  users: any[] = [];
  loading = true;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    this.http.get<any>(`${environment.apiUrl}/admin/users`).subscribe({
      next: (response) => {
        this.users = response.data || [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading users:', err);
        this.users = [];
        this.loading = false;
      }
    });
  }
}
