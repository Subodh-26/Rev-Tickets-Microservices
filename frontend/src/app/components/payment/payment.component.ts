import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';

declare var Razorpay: any;

interface ZoneBooking {
  zoneName: string;
  quantity: number;
  pricePerTicket: number;
}

interface PaymentRequest {
  showId?: number;
  openShowId?: number;
  isOpenEvent?: boolean;
  seatNumbers?: string[];
  zoneBookings?: ZoneBooking[];
  totalAmount: number;
}

interface OrderResponse {
  orderId: string;
  bookingId: number;
  amount: string;
  currency: string;
  key: string;
}

@Component({
  selector: 'app-payment',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './payment.component.html',
  styleUrl: './payment.component.css'
})
export class PaymentComponent implements OnInit {
  showId?: number;
  openShowId?: number;
  isOpenEvent: boolean = false;
  seatNumbers: string[] = [];
  zoneBookings: ZoneBooking[] = [];
  totalAmount: number = 0;
  loading: boolean = false;

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private http: HttpClient
  ) {}

  ngOnInit() {
    // Get query params
    this.route.queryParams.subscribe(params => {
      this.isOpenEvent = params['isOpenEvent'] === 'true';
      this.totalAmount = parseFloat(params['totalAmount'] || '0');

      if (this.isOpenEvent) {
        this.openShowId = parseInt(params['openShowId']);
        this.zoneBookings = JSON.parse(params['zoneBookings'] || '[]');
      } else {
        this.showId = parseInt(params['showId']);
        this.seatNumbers = JSON.parse(params['seatNumbers'] || '[]');
      }
    });

    // Load Razorpay script
    this.loadRazorpayScript();
  }

  loadRazorpayScript() {
    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.async = true;
    document.body.appendChild(script);
  }

  initiatePayment() {
    this.loading = true;

    const paymentRequest: any = {
      totalAmount: this.totalAmount,
      userId: localStorage.getItem('userId') || '1'
    };

    if (this.isOpenEvent) {
      paymentRequest.openShowId = this.openShowId;
      paymentRequest.zoneBookings = this.zoneBookings;
    } else {
      paymentRequest.showId = this.showId;
      paymentRequest.seatNumbers = this.seatNumbers;
    }

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    this.http.post<any>(`${environment.apiUrl}/payments/create-order`, paymentRequest, { headers })
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.openRazorpayCheckout(response.data);
          } else {
            alert('Failed to create order: ' + response.message);
            this.loading = false;
          }
        },
        error: (error) => {
          console.error('Error creating order:', error);
          alert('Failed to create payment order');
          this.loading = false;
        }
      });
  }

  createBooking(paymentId: string) {
    const bookingData: any = {
      userId: localStorage.getItem('userId') || '1',
      showId: this.showId,
      totalAmount: this.totalAmount,
      paymentId: paymentId,
      seatNumbers: this.seatNumbers
    };

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    this.http.post<any>(`${environment.apiUrl}/bookings`, bookingData, { headers })
      .subscribe({
        next: (response) => {
          this.loading = false;
          if (response.success) {
            alert('Booking confirmed!');
            this.router.navigate(['/']);
          } else {
            alert('Booking failed: ' + response.message);
          }
        },
        error: (error) => {
          this.loading = false;
          console.error('Error creating booking:', error);
          alert('Booking failed');
        }
      });
  }

  openRazorpayCheckout(orderData: OrderResponse) {
    console.log('🔴 Opening Razorpay checkout');
    console.log('🔴 Order data:', orderData);
    
    const options: any = {
      key: orderData.key,
      amount: orderData.amount,
      currency: orderData.currency,
      name: 'RevTickets',
      description: this.isOpenEvent ? 'Event Tickets' : 'Movie Tickets',
      order_id: orderData.orderId,
      handler: (response: any) => {
        console.log('🔴 Payment successful:', response);
        this.verifyPayment(response, orderData.bookingId);
      },
      prefill: {
        name: localStorage.getItem('username') || '',
        email: localStorage.getItem('email') || ''
      },
      method: {
        upi: true,
        card: true,
        netbanking: false,
        wallet: false,
        paylater: false,
        emi: false
      },
      theme: {
        color: '#3399cc'
      },
      modal: {
        ondismiss: () => {
          console.log('🔴 Payment modal dismissed');
          this.loading = false;
        }
      }
    };

    console.log('🔴 Razorpay options being used:', JSON.stringify(options, null, 2));
    
    const razorpay = new Razorpay(options);
    console.log('🔴 Razorpay instance created, opening modal...');
    razorpay.open();
  }

  verifyPayment(razorpayResponse: any, bookingId: number) {
    // Create booking after successful payment
    this.createBooking(razorpayResponse.razorpay_payment_id);
  }

  getBookingSummary(): string {
    if (this.isOpenEvent) {
      return this.zoneBookings.map(z => `${z.zoneName} (${z.quantity})`).join(', ');
    } else {
      return this.seatNumbers.join(', ');
    }
  }

  cancelPayment(bookingId: number) {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    this.http.post<any>(`${environment.apiUrl}/payments/cancel/${bookingId}`, {}, { headers })
      .subscribe({
        next: (response) => {
          console.log('Payment cancelled:', response);
          alert('Payment cancelled');
          this.router.navigate(['/']);
        },
        error: (error) => {
          console.error('Error cancelling payment:', error);
        }
      });
  }

  getTotalTickets(): number {
    if (this.isOpenEvent) {
      return this.zoneBookings.reduce((sum, z) => sum + z.quantity, 0);
    } else {
      return this.seatNumbers.length;
    }
  }
}
