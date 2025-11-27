# 🎯 REVTICKETS - HASSLE-FREE SETUP GUIDE

## ✅ PREREQUISITES CHECKLIST

Before starting, ensure you have:

- ✅ Java 17 or higher installed
- ✅ MySQL 8.0+ installed and running
- ✅ MongoDB 6.0+ installed and running
- ✅ Node.js 18+ and npm installed
- ✅ Maven 3.6+ installed

---

## 🚀 STEP 1: DATABASE SETUP

### MySQL Setup

```powershell
# Open MySQL Command Line Client or MySQL Workbench
# Login as root user

# Create database
CREATE DATABASE revticketsnew;

# Import the schema
# Option A: Using MySQL Command Line
mysql -u root -p revticketsnew < "c:\Users\dell\Desktop\revtickets_new\database_schema.sql"

# Option B: Using MySQL Workbench
# File > Run SQL Script > Select database_schema.sql > Execute
```

### MongoDB Setup

```powershell
# Start MongoDB service (if not already running)
net start MongoDB

# Verify MongoDB is running on default port 27017
mongosh --eval "db.version()"
```

**✅ Database Verification:**
- MySQL database `revticketsnew` created with 12 tables
- Admin user credentials: `admin@revtickets.com` / `admin123`
- MongoDB running on `mongodb://localhost:27017`

---

## 🚀 STEP 2: BACKEND SETUP

```powershell
# Navigate to backend directory
cd "c:\Users\dell\Desktop\revtickets_new\backend"

# Clean and build the project (optional but recommended)
mvn clean install -DskipTests

# Run the Spring Boot application
mvn spring-boot:run
```

**✅ Backend Verification:**
- Server starts on `http://localhost:8080`
- Console shows "Started RevticketsApplication"
- No errors in console logs

**🔍 Test Backend API:**
```powershell
# Test health endpoint (open in browser or use curl)
curl http://localhost:8080/api/movies
```

**⚠️ KEEP THIS TERMINAL RUNNING!** Backend must stay running.

---

## 🚀 STEP 3: FRONTEND SETUP

**Open a NEW PowerShell terminal** (keep backend running in first terminal)

```powershell
# Navigate to frontend directory
cd "c:\Users\dell\Desktop\revtickets_new\frontend"

# Dependencies already installed (npm install was done)
# Start Angular development server
ng serve
```

**OR use npm command:**
```powershell
npm start
```

**✅ Frontend Verification:**
- Server starts on `http://localhost:4200`
- Console shows "Compiled successfully"
- No compilation errors

**⚠️ KEEP THIS TERMINAL RUNNING TOO!** Frontend must stay running.

---

## 🌐 STEP 4: ACCESS THE APPLICATION

### Open your browser and navigate to:

```
http://localhost:4200
```

### 🎫 LOGIN CREDENTIALS:

**Admin Account:**
- Email: `admin@revtickets.com`
- Password: `admin123`

**Regular User Account (create via registration):**
- Click "Sign up" on login page
- Fill in your details
- Use the created credentials to login

---

## 🧪 STEP 5: TEST THE APPLICATION

### User Flow Test:
1. ✅ Open http://localhost:4200
2. ✅ Click "Browse Movies" or "Browse Events"
3. ✅ View movie/event details
4. ✅ Click "Login" → Use `admin@revtickets.com` / `admin123`
5. ✅ After login, browse movies/events again
6. ✅ Click "My Bookings" to see bookings (will be empty initially)
7. ✅ Click "Profile" to view your profile
8. ✅ **If logged in as admin:** Click "Admin Dashboard" to see admin panel

### Admin Flow Test:
1. ✅ Login as admin (`admin@revtickets.com` / `admin123`)
2. ✅ Click "Profile" → "Admin Dashboard"
3. ✅ View dashboard statistics (users, movies, bookings, revenue)
4. ✅ View booking status breakdown

---

## 📊 API ENDPOINTS REFERENCE

### Public Endpoints (No Auth Required):
```
GET  http://localhost:8080/api/movies
GET  http://localhost:8080/api/movies/now-showing
GET  http://localhost:8080/api/movies/{id}
GET  http://localhost:8080/api/events
GET  http://localhost:8080/api/events/upcoming
POST http://localhost:8080/api/auth/register
POST http://localhost:8080/api/auth/login
```

### Protected Endpoints (Auth Required):
```
GET  http://localhost:8080/api/bookings/my-bookings
GET  http://localhost:8080/api/users/profile
```

### Admin Endpoints (Admin Auth Required):
```
GET  http://localhost:8080/api/admin/dashboard/stats
POST http://localhost:8080/api/admin/movies
PUT  http://localhost:8080/api/admin/movies/{id}
DELETE http://localhost:8080/api/admin/movies/{id}
```

---

## 🛠️ TROUBLESHOOTING

### Issue: Backend fails to start

**Solution 1: Check MySQL connection**
```powershell
# Verify MySQL is running
mysql -u root -p -e "SHOW DATABASES;"
```

**Solution 2: Check port 8080**
```powershell
# Check if port 8080 is already in use
netstat -ano | findstr :8080

# Kill process if needed (replace PID with actual process ID)
taskkill /PID <PID> /F
```

### Issue: Frontend fails to compile

**Solution: Clear cache and reinstall**
```powershell
cd "c:\Users\dell\Desktop\revtickets_new\frontend"
rm -r node_modules
rm package-lock.json
npm install
ng serve
```

### Issue: MongoDB connection error

**Solution: Start MongoDB service**
```powershell
# Start MongoDB
net start MongoDB

# Check MongoDB status
mongosh --eval "db.runCommand({ ping: 1 })"
```

### Issue: CORS errors in browser console

**Solution:** Backend has CORS configured for `http://localhost:4200`. If you changed the frontend port, update `WebConfig.java` in backend.

---

## 📝 QUICK REFERENCE COMMANDS

### Start Everything (3 separate terminals):

**Terminal 1 - MySQL/MongoDB:**
```powershell
# Ensure MySQL service is running
# Ensure MongoDB service is running
net start MongoDB
```

**Terminal 2 - Backend:**
```powershell
cd "c:\Users\dell\Desktop\revtickets_new\backend"
mvn spring-boot:run
```

**Terminal 3 - Frontend:**
```powershell
cd "c:\Users\dell\Desktop\revtickets_new\frontend"
ng serve
```

### Access Points:
- Frontend: http://localhost:4200
- Backend API: http://localhost:8080/api
- MySQL: localhost:3306 (database: revticketsnew)
- MongoDB: localhost:27017 (database: revtickets_mongo)

---

## 🎉 SUCCESS INDICATORS

You'll know everything is working when:

✅ Backend terminal shows: `Started RevticketsApplication in X seconds`
✅ Frontend terminal shows: `Compiled successfully`
✅ Browser opens http://localhost:4200 with RevTickets homepage
✅ You can see movies/events on the homepage
✅ Login works with admin@revtickets.com / admin123
✅ Admin dashboard shows statistics (users, movies, bookings)

---

## 🔐 DEFAULT SAMPLE DATA

After running `database_schema.sql`, you'll have:

**Users:**
- Admin: admin@revtickets.com / admin123

**Venues:**
- PVR Cinemas Phoenix
- INOX Multiplex Central Mall
- BookMyShow Live Arena

**Movies:**
- Inception, The Dark Knight, Interstellar, Avengers Endgame, Avatar

**Events:**
- Coldplay Live in Concert
- IPL 2024 Finals

**Shows:**
- Multiple show times for each movie/event across different venues

---

## 📞 NEED HELP?

If you encounter issues:

1. Check all prerequisites are installed correctly
2. Ensure databases are running (MySQL + MongoDB)
3. Verify no port conflicts (8080, 4200, 3306, 27017)
4. Check console logs for specific error messages
5. Try the troubleshooting steps above

**Happy Booking! 🎬🎟️**
