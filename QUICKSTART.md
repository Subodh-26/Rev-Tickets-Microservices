# 🚀 RevTickets - Quick Start Guide

## ✅ What's Been Created

### Backend Files (Complete Foundation)

#### 📊 Database (100% Complete)
- ✅ `database_schema.sql` - Complete MySQL schema with 12 tables
- ✅ Sample data (admin user, 3 venues, 1 seat layout)

#### 🏗️ Core Structure (100% Complete)
- ✅ `pom.xml` - All Maven dependencies
- ✅ `application.properties` - Configuration
- ✅ `RevTicketsApplication.java` - Main class

#### 🗂️ Entities (100% Complete - 11 classes)
- ✅ User.java
- ✅ Venue.java
- ✅ SeatLayout.java
- ✅ Movie.java
- ✅ Event.java
- ✅ Show.java
- ✅ Seat.java
- ✅ Booking.java
- ✅ BookingSeat.java
- ✅ Payment.java
- ✅ Cart.java
- ✅ Banner.java

#### 📚 MongoDB Documents (100% Complete - 3 classes)
- ✅ Review.java
- ✅ Notification.java
- ✅ ActivityLog.java

#### 🗄️ Repositories (100% Complete - 15 interfaces)
**MySQL Repositories:**
- ✅ UserRepository
- ✅ VenueRepository
- ✅ SeatLayoutRepository
- ✅ MovieRepository
- ✅ EventRepository
- ✅ ShowRepository
- ✅ SeatRepository
- ✅ BookingRepository
- ✅ BookingSeatRepository
- ✅ PaymentRepository
- ✅ CartRepository
- ✅ BannerRepository

**MongoDB Repositories:**
- ✅ ReviewRepository
- ✅ NotificationRepository
- ✅ ActivityLogRepository

#### 🔐 Security (100% Complete - 4 classes)
- ✅ CustomUserDetails.java
- ✅ UserDetailsServiceImpl.java
- ✅ JwtTokenProvider.java
- ✅ JwtAuthenticationFilter.java

#### ⚙️ Configuration (100% Complete - 2 classes)
- ✅ SecurityConfig.java
- ✅ WebConfig.java

#### 📦 DTOs (100% Complete - 4 classes)
- ✅ RegisterRequest.java
- ✅ LoginRequest.java
- ✅ AuthResponse.java
- ✅ ApiResponse.java

#### 🎯 Services (Started - 1 of 13)
- ✅ AuthService.java

#### 🌐 Controllers (Started - 1 of 15)
- ✅ AuthController.java

---

## 🏃‍♂️ How to Run (Step by Step)

### Step 1: Start Databases

```powershell
# Start MySQL
net start MySQL80

# Start MongoDB
net start MongoDB
```

### Step 2: Create Database

```powershell
# Open MySQL
mysql -u root -p

# Run in MySQL:
source C:/Users/dell/Desktop/revtickets_new/backend/database_schema.sql
exit
```

### Step 3: Configure Backend

Edit `backend/src/main/resources/application.properties`:

```properties
# Update these if different
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

# Add your Stripe test key
stripe.api.key=sk_test_YOUR_STRIPE_KEY
```

### Step 4: Run Backend

```powershell
cd C:\Users\dell\Desktop\revtickets_new\backend
mvn spring-boot:run
```

**Server starts on:** http://localhost:8080

### Step 5: Test Authentication API

#### Register a User
```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "Test@123",
  "fullName": "Test User",
  "phone": "9876543210"
}
```

#### Login
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "admin@revtickets.com",
  "password": "Admin@123"
}
```

Response will include JWT token:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "admin@revtickets.com",
  "fullName": "Admin User",
  "role": "ADMIN"
}
```

#### Get Profile (Authenticated)
```bash
GET http://localhost:8080/api/auth/profile
Authorization: Bearer YOUR_JWT_TOKEN
```

---

## 🎯 What Works Now

✅ **User Registration** - Users can sign up  
✅ **User Login** - JWT tokens generated  
✅ **Authentication** - JWT validation on protected routes  
✅ **Authorization** - Role-based access (ADMIN/CUSTOMER)  
✅ **Get Profile** - Fetch logged-in user details  
✅ **Password Encryption** - BCrypt hashing  
✅ **CORS** - Frontend can connect  
✅ **Database Connection** - MySQL & MongoDB ready  

---

## 📋 What Still Needs to be Built

### Priority 1: Core Services & Controllers (70% of work)

#### Services Needed:
1. ⏳ MovieService - CRUD for movies
2. ⏳ EventService - CRUD for events  
3. ⏳ VenueService - Venue management
4. ⏳ ShowService - Show time management
5. ⏳ SeatService - Seat allocation logic
6. ⏳ BookingService - Booking creation
7. ⏳ PaymentService - Stripe integration
8. ⏳ FileUploadService - Image uploads
9. ⏳ QRCodeService - QR generation
10. ⏳ NotificationService - WebSocket
11. ⏳ ReviewService - MongoDB reviews
12. ⏳ DashboardService - Admin analytics

#### Controllers Needed:
1. ⏳ MovieController - GET /api/movies
2. ⏳ EventController - GET /api/events
3. ⏳ ShowController - GET /api/shows
4. ⏳ VenueController - GET /api/venues
5. ⏳ BannerController - GET /api/banners
6. ⏳ BookingController - POST /api/bookings
7. ⏳ PaymentController - POST /api/payments
8. ⏳ ReviewController - GET/POST /api/reviews
9. ⏳ AdminMovieController - POST /api/admin/movies
10. ⏳ AdminEventController - POST /api/admin/events
11. ⏳ AdminShowController - POST /api/admin/shows
12. ⏳ AdminVenueController - POST /api/admin/venues
13. ⏳ AdminDashboardController - GET /api/admin/dashboard
14. ⏳ AdminSeatLayoutController - POST /api/admin/seat-layouts

### Priority 2: Frontend (20% of work)

Need to create Angular app with:
- Login/Register pages
- Home page with movies/events
- Movie/Event detail pages
- Seat selection page
- Payment page
- My Bookings page
- Admin dashboard
- Admin CRUD pages

### Priority 3: Utilities & Extras (10% of work)

- QR Code generation
- File upload handling
- WebSocket configuration
- Redis caching
- Exception handling

---

## 💡 Next Immediate Steps

### Option A: Complete Backend First (Recommended)

1. **Create MovieService & MovieController**
   - Add CRUD operations for movies
   - File upload for posters/banners
   - Public endpoint to list movies

2. **Create EventService & EventController**
   - Similar to MovieService

3. **Create ShowService & ShowController**
   - Link movies/events to venues
   - Manage show times

4. **Create BookingService & PaymentService**
   - Booking flow
   - Stripe integration

5. **Test all APIs with Postman**

6. **Then build Angular frontend**

### Option B: Build Incrementally

1. Create MovieService/Controller
2. Build Angular movie listing page
3. Create EventService/Controller
4. Build Angular event listing page
5. Continue feature by feature

---

## 📁 File Structure Summary

```
revtickets_new/
├── backend/
│   ├── src/main/java/com/revature/revtickets/
│   │   ├── entity/          ✅ 12 files (DONE)
│   │   ├── document/        ✅ 3 files (DONE)
│   │   ├── repository/      ✅ 15 files (DONE)
│   │   ├── security/        ✅ 4 files (DONE)
│   │   ├── config/          ✅ 2 files (DONE)
│   │   ├── dto/             ✅ 4 files (DONE)
│   │   ├── service/         🟡 1/13 files (8% DONE)
│   │   ├── controller/      🟡 1/15 files (7% DONE)
│   │   ├── util/            ❌ 0/4 files (NOT STARTED)
│   │   └── exception/       ❌ 0/4 files (NOT STARTED)
│   ├── src/main/resources/
│   │   └── application.properties  ✅ (DONE)
│   ├── public/images/       ✅ (FOLDER CREATED)
│   ├── pom.xml              ✅ (DONE)
│   └── database_schema.sql  ✅ (DONE)
└── frontend/                ❌ (NOT STARTED)
```

**Overall Progress: ~35% Complete**

---

## 🎓 Learning Path

If you're new to this stack:

### Backend
1. Study Spring Boot basics
2. Understand JPA/Hibernate
3. Learn JWT authentication
4. Practice REST API design

### Frontend
1. Learn Angular basics
2. Practice component communication
3. Understand services and HTTP
4. Learn Angular routing

### Integration
1. API testing with Postman
2. CORS understanding
3. JWT token handling in frontend
4. Stripe payment flow

---

## 🔗 Useful Commands

```powershell
# Backend
cd backend
mvn clean install              # Build
mvn spring-boot:run           # Run
mvn test                      # Test

# Check if running
curl http://localhost:8080/api/auth/profile

# Database
mysql -u root -p              # MySQL CLI
mongosh                       # MongoDB CLI

# Ports
netstat -ano | findstr :8080  # Check port 8080
netstat -ano | findstr :3306  # MySQL
netstat -ano | findstr :27017 # MongoDB
```

---

## 🐛 Common Issues & Solutions

### Issue: MySQL Connection Error
**Solution:** 
- Start MySQL service: `net start MySQL80`
- Check credentials in `application.properties`
- Run `database_schema.sql`

### Issue: MongoDB Connection Error
**Solution:**
- Start MongoDB: `net start MongoDB`
- Check port 27017 is free
- Default connection string works for local

### Issue: Port 8080 Already in Use
**Solution:**
```powershell
netstat -ano | findstr :8080
taskkill /PID <PID_NUMBER> /F
```

### Issue: JWT Token Invalid
**Solution:**
- Check `jwt.secret` in `application.properties`
- Ensure token is sent as: `Authorization: Bearer <token>`
- Token expires after 24 hours (configurable)

---

## ✨ Features Ready to Use

1. ✅ **Secure Registration & Login**
2. ✅ **JWT Token Generation**
3. ✅ **Role-Based Access Control**
4. ✅ **Password Encryption**
5. ✅ **User Profile Retrieval**

---

## 🎯 Your Mission

**Goal:** Build a complete ticket booking platform!

**Current Status:** Foundation is solid ✅  
**Next Step:** Implement services and controllers  
**Timeline:** Depends on your pace (estimated 1-2 weeks for fullstack)

**Remember:** You've got 35% done already. The hardest parts (database design, security setup) are complete!

---

## 📞 Need Help?

1. Check `IMPLEMENTATION_ROADMAP.md` for detailed task list
2. Refer to `SETUP_GUIDE.md` for installation steps
3. Test APIs with Postman collection (create one!)
4. Use `README.md` for project overview

---

**You're all set to build something amazing! 🚀**

Focus on one feature at a time, test thoroughly, and you'll have a production-ready application!
