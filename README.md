# RevTickets - Monolithic Ticket Booking Application

## Overview
RevTickets is a full-stack monolithic web application for booking tickets to movies, events, concerts, and travel. Built with Spring Boot backend and Angular frontend, it demonstrates polyglot persistence using MySQL and MongoDB.

## Technology Stack

### Backend
- **Spring Boot 3.2.0**
- **Spring Data JPA** (MySQL)
- **Spring Data MongoDB**
- **Spring Security** (JWT Authentication)
- **WebSockets** (Real-time notifications)
- **Stripe API** (Payment integration)
- **Redis** (Optional caching)
- **ZXing** (QR Code generation)

### Frontend
- **Angular 17+**
- **Tailwind CSS**
- **TypeScript**

### Databases
- **MySQL** - Structured data (users, bookings, payments, shows)
- **MongoDB** - Unstructured data (reviews, notifications, logs)

## Prerequisites
- Java 17+
- Node.js 18+
- MySQL 8.0+
- MongoDB 6.0+
- Maven 3.8+
- Redis (Optional)

## Database Setup

### MySQL Setup
```bash
# Login to MySQL
mysql -u root -p

# Run the schema file
source backend/database_schema.sql
```

### MongoDB Setup
MongoDB collections will be auto-created when the application runs.

## Backend Setup

### 1. Configure Application Properties
Edit `backend/src/main/resources/application.properties`:
```properties
# Update MySQL credentials
spring.datasource.username=your_username
spring.datasource.password=your_password

# Update Stripe API key
stripe.api.key=sk_test_your_stripe_key_here
```

### 2. Build and Run
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend will start on `http://localhost:8080`

## Frontend Setup

### 1. Install Dependencies
```bash
cd frontend
npm install
```

### 2. Run Development Server
```bash
npm start
```

Frontend will start on `http://localhost:4200`

## Default Credentials

### Admin Login
- **Email**: admin@revtickets.com
- **Password**: Admin@123

## Key Features

### User Features
- ✅ User registration and JWT authentication
- ✅ Browse movies and events
- ✅ View show times and venues
- ✅ Interactive seat selection
- ✅ Stripe payment integration
- ✅ Booking history with QR codes
- ✅ Reviews and ratings
- ✅ Real-time notifications (WebSocket)

### Admin Features
- ✅ Dashboard with analytics
- ✅ Add/Edit/Delete movies and events
- ✅ Manage venues and screens
- ✅ Create custom seat layouts
- ✅ Manage show times dynamically
- ✅ View booking reports

## API Endpoints

### Authentication
```
POST /api/auth/register
POST /api/auth/login
GET  /api/auth/profile
```

### Movies (Public)
```
GET    /api/movies
GET    /api/movies/{id}
GET    /api/movies/{id}/shows
```

### Movies (Admin)
```
POST   /api/admin/movies
PUT    /api/admin/movies/{id}
DELETE /api/admin/movies/{id}
```

### Events
```
GET    /api/events
GET    /api/events/{id}
POST   /api/admin/events (Admin only)
```

### Shows (Admin)
```
POST   /api/admin/shows
PUT    /api/admin/shows/{id}
DELETE /api/admin/shows/{id}
```

### Bookings
```
POST   /api/bookings/create
GET    /api/bookings/my-bookings
GET    /api/bookings/{id}
```

### Payments
```
POST   /api/payments/create-intent
POST   /api/payments/confirm
```

### Seat Management
```
GET    /api/shows/{id}/seats
POST   /api/seats/lock (Temporary hold)
```

## Project Structure

```
revtickets_new/
├── backend/
│   ├── src/main/java/com/revature/revtickets/
│   │   ├── entity/          # JPA entities
│   │   ├── document/        # MongoDB documents
│   │   ├── repository/      # Repositories
│   │   ├── service/         # Business logic
│   │   ├── controller/      # REST controllers
│   │   ├── config/          # Configuration
│   │   ├── security/        # JWT & Security
│   │   ├── dto/             # Data Transfer Objects
│   │   └── util/            # Utilities (QR, File upload)
│   ├── public/images/       # Image storage
│   └── pom.xml
└── frontend/
    ├── src/app/
    │   ├── components/      # UI components
    │   ├── services/        # API services
    │   ├── guards/          # Auth guards
    │   ├── models/          # TypeScript interfaces
    │   └── pages/           # Route pages
    └── package.json
```

## Stripe Testing

Use Stripe test mode with test cards:
- **Success**: 4242 4242 4242 4242
- **Decline**: 4000 0000 0000 0002
- **CVC**: Any 3 digits
- **Expiry**: Any future date

## WebSocket Connection

Connect to: `ws://localhost:8080/ws`

Subscribe to user notifications:
```javascript
stompClient.subscribe('/user/queue/notifications', callback);
```

## Redis Caching (Optional)

If Redis is running, seat availability will be cached for improved performance.

## Image Upload

Images are stored in `/backend/public/images/`:
- `/movies/` - Movie posters and banners
- `/events/` - Event images
- `/banners/` - Homepage banners

## Development Notes

### Adding a New Movie (Admin)
1. Login as admin
2. Navigate to Admin Dashboard > Movies > Add Movie
3. Fill in details and upload images
4. Create show times with venue and seat layout

### Creating Custom Seat Layouts
1. Admin Dashboard > Seat Layouts > Create New
2. Define rows, columns, and seat types
3. Assign pricing for each seat category

### Booking Flow
1. User browses movies/events
2. Selects show time and seats
3. Seats are temporarily locked (10 min)
4. Payment via Stripe
5. QR code generated on success
6. WebSocket notification sent

## Troubleshooting

### MySQL Connection Error
- Verify MySQL is running
- Check credentials in `application.properties`
- Ensure database `revticketsnew` exists

### MongoDB Connection Error
- Start MongoDB service
- Check port 27017 is available

### Stripe Payment Fails
- Verify test API key is set
- Check browser console for errors
- Ensure test card numbers are used

## Future Enhancements
- Email notifications
- Password reset functionality
- Ticket refunds
- Multi-language support
- Mobile app

## Contributors
Built with ❤️ by Revature Team

## License
MIT License
