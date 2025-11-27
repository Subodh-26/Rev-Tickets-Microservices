# RevTickets - Complete Setup Guide

## 🚀 Project Overview
RevTickets is a complete monolithic ticket booking platform built with **Spring Boot 3.2.0** backend and **Angular 17+** frontend. Book movie tickets, event tickets, manage shows, venues, and payments seamlessly.

---

## 📋 Prerequisites

### Required Software:
- **Java 17** or higher ([Download](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html))
- **Node.js 18+** and npm ([Download](https://nodejs.org/))
- **MySQL 8.0+** ([Download](https://dev.mysql.com/downloads/mysql/))
- **MongoDB 6.0+** ([Download](https://www.mongodb.com/try/download/community))
- **Maven 3.8+** (usually bundled with Spring Boot)
- **Angular CLI 17+**: `npm install -g @angular/cli`

### Optional:
- **Redis 7+** for caching ([Download](https://redis.io/download))
- **Stripe Account** for payment testing ([Signup](https://dashboard.stripe.com/register))

---

## 🗄️ Database Setup

### Step 1: Start MySQL Server
```bash
# Windows: Start MySQL service from Services or
net start MySQL80

# Verify MySQL is running
mysql --version
```

### Step 2: Create Database and Import Schema
```bash
# Login to MySQL
mysql -u root -p
# Enter password: root (or your MySQL root password)

# Create database
CREATE DATABASE revticketsnew;

# Exit MySQL shell
EXIT;

# Import the schema
cd c:\Users\dell\Desktop\revtickets_new\backend
mysql -u root -p revticketsnew < database_schema.sql
```

**Database Details:**
- **Database Name**: `revticketsnew`
- **Username**: `root`
- **Password**: `root` (change in `application.properties` if different)

### Step 3: Start MongoDB Server
```bash
# Windows: Start MongoDB service from Services or
net start MongoDB

# Verify MongoDB is running
mongosh --version

# Connect to MongoDB
mongosh

# Switch to database
use revtickets_mongo

# Verify connection (should show empty collections initially)
show collections

# Exit
exit
```

**MongoDB Details:**
- **Database Name**: `revtickets_mongo`
- **Host**: `localhost`
- **Port**: `27017`

---

## ⚙️ Backend Setup (Spring Boot)

### Step 1: Navigate to Backend Directory
```powershell
cd c:\Users\dell\Desktop\revtickets_new\backend
```

### Step 2: Configure Stripe (Optional but Recommended)
1. Go to [Stripe Dashboard](https://dashboard.stripe.com/test/apikeys)
2. Copy your **Secret Key** (starts with `sk_test_`)
3. Open `src/main/resources/application.properties`
4. Replace:
   ```properties
   stripe.api.key=sk_test_your_actual_stripe_key_here
   ```

### Step 3: Build the Project
```powershell
mvn clean install -DskipTests
```

### Step 4: Run the Backend
```powershell
mvn spring-boot:run
```

**Backend will start at:** `http://localhost:8080`

### Test Backend APIs:
Open browser or Postman and test:
- Health Check: `http://localhost:8080/api/movies`
- Login: POST `http://localhost:8080/api/auth/login`
  ```json
  {
    "email": "admin@revtickets.com",
    "password": "admin123"
  }
  ```

---

## 🎨 Frontend Setup (Angular)

### Step 1: Navigate to Frontend Directory
```powershell
cd c:\Users\dell\Desktop\revtickets_new\frontend
```

### Step 2: Install Dependencies
```powershell
npm install
```

### Step 3: Start Development Server
```powershell
ng serve
```

**Frontend will start at:** `http://localhost:4200`

---

## 🔐 Default Admin Credentials
```
Email: admin@revtickets.com
Password: admin123
```

---

## 📁 Project Structure

### Backend (`backend/`)
```
backend/
├── src/main/java/com/revature/revtickets/
│   ├── config/          # Security, CORS, WebSocket configs
│   ├── controller/      # REST API endpoints
│   │   ├── admin/       # Admin-only endpoints
│   │   ├── AuthController.java
│   │   ├── MovieController.java
│   │   ├── EventController.java
│   │   ├── BookingController.java
│   │   └── ...
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA Entities (MySQL)
│   ├── document/        # MongoDB Documents
│   ├── repository/      # Database repositories
│   ├── service/         # Business logic
│   ├── util/            # Utilities (QR, File Upload, etc.)
│   ├── exception/       # Custom exceptions
│   └── response/        # Response wrappers
├── src/main/resources/
│   └── application.properties
├── database_schema.sql
└── pom.xml
```

### Frontend (`frontend/`)
```
frontend/
├── src/app/
│   ├── core/            # Auth services, guards, interceptors
│   ├── shared/          # Shared components, pipes, directives
│   ├── features/
│   │   ├── auth/        # Login, Register
│   │   ├── home/        # Homepage with banners
│   │   ├── movies/      # Movie listing, details
│   │   ├── events/      # Event listing, details
│   │   ├── booking/     # Seat selection, payment
│   │   ├── profile/     # User profile, bookings
│   │   └── admin/       # Admin dashboard, CRUD
│   └── app.routes.ts    # Angular routing
├── tailwind.config.js
└── angular.json
```

---

## 🧪 Testing the Application

### 1. **User Flow (Frontend)**
1. Open `http://localhost:4200`
2. Register a new account or login with admin credentials
3. Browse movies/events
4. Select a show → Choose seats → Proceed to payment
5. View "My Bookings" to see QR codes

### 2. **Admin Flow**
1. Login as admin: `admin@revtickets.com` / `admin123`
2. Access admin dashboard at `/admin`
3. Manage movies, events, shows, venues, banners
4. View analytics and reports

### 3. **API Testing (Postman)**
Import the following endpoints:

**Auth:**
- POST `/api/auth/register` - Register user
- POST `/api/auth/login` - Login
- GET `/api/auth/profile` - Get user profile (requires JWT)

**Movies:**
- GET `/api/movies` - All active movies
- GET `/api/movies/{id}` - Movie details
- GET `/api/movies/search?title=Avengers` - Search

**Bookings:**
- POST `/api/bookings` - Create booking (requires JWT)
  ```json
  {
    "showId": 1,
    "seatIds": [1, 2, 3]
  }
  ```
- GET `/api/bookings/my-bookings` - User bookings (requires JWT)

**Admin (requires ADMIN role):**
- POST `/api/admin/movies` - Create movie
- PUT `/api/admin/movies/{id}` - Update movie
- DELETE `/api/admin/movies/{id}` - Delete movie

---

## 🔧 Configuration Files

### Backend: `application.properties`
Key configurations:
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/revticketsnew
spring.datasource.username=root
spring.datasource.password=root

# MongoDB
spring.data.mongodb.database=revtickets_mongo

# JWT
jwt.secret=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
jwt.expiration=86400000

# Stripe
stripe.api.key=sk_test_your_stripe_key_here

# File Upload
file.upload.dir=public/images

# CORS
cors.allowed.origins=http://localhost:4200
```

### Frontend: `environment.ts`
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  stripePublicKey: 'pk_test_your_stripe_public_key_here'
};
```

---

## 🐛 Troubleshooting

### Backend won't start:
- **Error: "Access denied for user 'root'"**
  - Fix MySQL password in `application.properties`
- **Error: "Database doesn't exist"**
  - Run: `CREATE DATABASE revticketsnew;` in MySQL
- **Error: "Port 8080 already in use"**
  - Change port in `application.properties`: `server.port=8081`

### Frontend won't start:
- **Error: "Module not found"**
  - Delete `node_modules` and run `npm install` again
- **Error: "Port 4200 already in use"**
  - Run: `ng serve --port 4201`

### Database connection issues:
- Verify MySQL/MongoDB services are running
- Check firewall settings
- Test connection:
  ```bash
  mysql -u root -p -h localhost
  mongosh --host localhost --port 27017
  ```

---

## 📦 Tech Stack

### Backend:
- Spring Boot 3.2.0
- Spring Security with JWT
- Spring Data JPA (MySQL)
- Spring Data MongoDB
- Stripe Java SDK
- WebSocket (SockJS + STOMP)
- Lombok, BCrypt, ZXing (QR)

### Frontend:
- Angular 17+
- Tailwind CSS
- RxJS
- Angular Material (optional)
- Chart.js (for admin analytics)

---

## 🚢 Deployment

### Backend (JAR):
```bash
cd backend
mvn clean package -DskipTests
java -jar target/revtickets-0.0.1-SNAPSHOT.jar
```

### Frontend (Build):
```bash
cd frontend
ng build --configuration production
# Serve 'dist/' folder with Nginx or Apache
```

---

## 📞 Support
For issues, contact: **revtickets@support.com**

---

## 🎉 You're All Set!
Run both backend and frontend together:
```powershell
# Terminal 1 - Backend
cd c:\Users\dell\Desktop\revtickets_new\backend
mvn spring-boot:run

# Terminal 2 - Frontend
cd c:\Users\dell\Desktop\revtickets_new\frontend
ng serve
```

Visit: **http://localhost:4200** and start booking! 🎬🎫
