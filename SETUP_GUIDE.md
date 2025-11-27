# RevTickets - Complete Setup Guide

## 🎯 Quick Start (Step by Step)

### Step 1: Database Setup

#### MySQL Setup
```bash
# Start MySQL service
# Windows: Services → MySQL → Start
# Or via command line:
net start MySQL80

# Login to MySQL
mysql -u root -p

# Create database and run schema
source C:/Users/dell/Desktop/revtickets_new/backend/database_schema.sql
```

#### MongoDB Setup
```bash
# Start MongoDB service
# Windows: Services → MongoDB → Start
# Or via command line:
net start MongoDB

# Verify MongoDB is running
mongosh
show dbs
exit
```

### Step 2: Backend Setup

```bash
cd C:/Users/dell/Desktop/revtickets_new/backend

# Build the project
mvn clean install -DskipTests

# Run the application
mvn spring-boot:run
```

**Backend will run on:** `http://localhost:8080`

### Step 3: Frontend Setup

```bash
cd C:/Users/dell/Desktop/revtickets_new/frontend

# Install Angular CLI globally (if not installed)
npm install -g @angular/cli

# Create Angular project
ng new . --routing --style=css --skip-git

# Install dependencies
npm install
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init

# Run development server
ng serve
```

**Frontend will run on:** `http://localhost:4200`

---

## 📁 Project Structure Created

```
revtickets_new/
├── backend/
│   ├── src/main/
│   │   ├── java/com/revature/revtickets/
│   │   │   ├── entity/                  ✅ CREATED (11 entities)
│   │   │   ├── document/                ✅ CREATED (3 documents)
│   │   │   ├── repository/              ✅ CREATED (15 repositories)
│   │   │   ├── RevTicketsApplication.java ✅ CREATED
│   │   │   ├── service/                 ⏳ TO BE CREATED
│   │   │   ├── controller/              ⏳ TO BE CREATED
│   │   │   ├── security/                ⏳ TO BE CREATED
│   │   │   ├── config/                  ⏳ TO BE CREATED
│   │   │   ├── dto/                     ⏳ TO BE CREATED
│   │   │   └── util/                    ⏳ TO BE CREATED
│   │   └── resources/
│   │       └── application.properties   ✅ CREATED
│   ├── public/images/                   ✅ CREATED
│   ├── pom.xml                          ✅ CREATED
│   └── database_schema.sql              ✅ CREATED
└── frontend/                            ⏳ TO BE SET UP
```

---

## 🗃️ Database Schema Overview

### MySQL Tables (11 tables)
1. ✅ **users** - User authentication and profiles
2. ✅ **venues** - Theater/venue information
3. ✅ **seat_layouts** - Reusable seat configurations
4. ✅ **movies** - Movie details with metadata
5. ✅ **events** - Events (concerts, sports, etc.)
6. ✅ **shows** - Show timings for movies/events
7. ✅ **seats** - Individual seats per show
8. ✅ **bookings** - User booking records
9. ✅ **booking_seats** - Booked seat details
10. ✅ **payments** - Payment transactions
11. ✅ **banners** - Homepage carousel banners
12. ✅ **cart** - Temporary seat holds

### MongoDB Collections (3 collections)
1. ✅ **reviews** - Movie/event reviews
2. ✅ **notifications** - User notifications
3. ✅ **activity_logs** - User activity tracking

---

## 🔑 Default Admin Credentials

**Email:** admin@revtickets.com  
**Password:** Admin@123

---

## 🎨 Sample Data Included

The `database_schema.sql` includes:
- ✅ 1 Admin user
- ✅ 3 Sample venues (PVR, INOX, Cinepolis)
- ✅ 1 Standard seat layout (100 seats)

---

## 📦 Dependencies Installed

### Backend (pom.xml)
- Spring Boot Web
- Spring Data JPA (MySQL)
- Spring Data MongoDB
- Spring Security + JWT
- Stripe Java SDK
- WebSocket
- Redis (optional)
- ZXing (QR code generation)
- Lombok

### Frontend (to be installed)
- Angular 17+
- Tailwind CSS
- Angular Material (optional)
- ngx-stripe

---

## 🚀 Next Steps

### 1. Complete Backend Files
You need to create:
- Services (Business logic)
- Controllers (REST APIs)
- Security configuration (JWT)
- DTOs (Request/Response objects)
- Utilities (QR code, file upload)

### 2. Complete Frontend
- Generate Angular components
- Implement routing
- Create services for API calls
- Build UI matching the design

### 3. Configure Stripe
- Get Stripe test API key from: https://dashboard.stripe.com/test/apikeys
- Update `application.properties`:
```properties
stripe.api.key=sk_test_YOUR_KEY_HERE
```

### 4. Optional: Setup Redis
```bash
# Download Redis for Windows: https://github.com/microsoftarchive/redis/releases
# Start Redis server
redis-server
```

---

## 📞 API Testing

Use Postman or any REST client:

### Register User
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "User@123",
  "fullName": "John Doe",
  "phone": "9876543210"
}
```

### Login
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "admin@revtickets.com",
  "password": "Admin@123"
}
```

---

## ⚡ Quick Commands Reference

```bash
# Backend
cd backend
mvn spring-boot:run                    # Run backend
mvn clean install                      # Build project
mvn test                              # Run tests

# Frontend
cd frontend
ng serve                              # Run dev server
ng build                              # Production build
ng generate component <name>         # Create component
ng generate service <name>            # Create service

# MySQL
mysql -u root -p                      # Login
SHOW DATABASES;                       # List databases
USE revticketsnew;                    # Select database
SHOW TABLES;                          # List tables
DESC users;                           # Describe table

# MongoDB
mongosh                               # MongoDB shell
show dbs                              # List databases
use revtickets_mongo                  # Select database
show collections                      # List collections
db.reviews.find()                     # Query collection
```

---

## 🐛 Troubleshooting

### Port Already in Use
```bash
# Backend (8080)
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Frontend (4200)
netstat -ano | findstr :4200
taskkill /PID <PID> /F
```

### MySQL Connection Failed
1. Check MySQL service is running
2. Verify credentials in `application.properties`
3. Ensure port 3306 is not blocked

### MongoDB Connection Failed
1. Start MongoDB service
2. Check connection string
3. Verify port 27017

---

## 📚 Resources

- Spring Boot Docs: https://spring.io/projects/spring-boot
- Angular Docs: https://angular.io/docs
- Stripe API: https://stripe.com/docs/api
- Tailwind CSS: https://tailwindcss.com/docs

---

**Status: Backend Foundation Complete ✅**  
**Next: Implement Services, Controllers, and Frontend 🚀**
