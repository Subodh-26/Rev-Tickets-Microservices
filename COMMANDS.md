# 🎬 RevTickets - Command Cheat Sheet

## 🚀 Quick Start Commands

### Start Everything
```powershell
# 1. Start MySQL
net start MySQL80

# 2. Start MongoDB
net start MongoDB

# 3. Run Backend (in backend folder)
cd C:\Users\dell\Desktop\revtickets_new\backend
mvn spring-boot:run

# 4. Run Frontend (in frontend folder, after setup)
cd C:\Users\dell\Desktop\revtickets_new\frontend
ng serve
```

---

## 🗄️ Database Commands

### MySQL
```powershell
# Login
mysql -u root -p

# Create/Reset Database
source C:/Users/dell/Desktop/revtickets_new/backend/database_schema.sql

# Useful SQL Commands
USE revticketsnew;
SHOW TABLES;
DESC users;
SELECT * FROM users;
SELECT * FROM movies;
```

### MongoDB
```powershell
# Open MongoDB Shell
mongosh

# Useful Commands
show dbs
use revtickets_mongo
show collections
db.reviews.find()
db.notifications.find()
db.reviews.countDocuments()
```

---

## 🔧 Backend (Maven) Commands

```powershell
# Navigate to backend
cd C:\Users\dell\Desktop\revtickets_new\backend

# Clean and build
mvn clean install

# Run application
mvn spring-boot:run

# Run without tests
mvn spring-boot:run -DskipTests

# Run tests only
mvn test

# Package as JAR
mvn package

# Clean target folder
mvn clean
```

---

## 🎨 Frontend (Angular) Commands

```powershell
# Navigate to frontend
cd C:\Users\dell\Desktop\revtickets_new\frontend

# Initial setup (one time)
ng new . --routing --style=css --skip-git
npm install
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init

# Run development server
ng serve
# Opens on http://localhost:4200

# Build for production
ng build

# Run tests
ng test

# Generate component
ng generate component components/component-name
ng g c components/component-name  # shorthand

# Generate service
ng generate service services/service-name
ng g s services/service-name  # shorthand

# Generate guard
ng generate guard guards/guard-name

# Generate interface
ng generate interface models/model-name
```

---

## 🧪 API Testing (Postman/cURL)

### Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"test@example.com\",\"password\":\"Test@123\",\"fullName\":\"Test User\",\"phone\":\"9876543210\"}"
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"admin@revtickets.com\",\"password\":\"Admin@123\"}"
```

### Get Profile (with token)
```bash
curl -X GET http://localhost:8080/api/auth/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## 🔍 Debugging Commands

### Check if services are running
```powershell
# Backend
curl http://localhost:8080/api/auth/profile

# Frontend
curl http://localhost:4200

# MySQL
netstat -ano | findstr :3306

# MongoDB
netstat -ano | findstr :27017

# Backend
netstat -ano | findstr :8080

# Frontend
netstat -ano | findstr :4200
```

### Kill process on port
```powershell
# Find PID
netstat -ano | findstr :8080

# Kill process (replace <PID> with actual number)
taskkill /PID <PID> /F
```

---

## 📦 Git Commands

```powershell
# Initialize repository
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit - Backend foundation complete"

# Check status
git status

# View changes
git diff

# Create branch
git checkout -b feature/movie-service

# Switch branch
git checkout main

# Merge branch
git merge feature/movie-service
```

---

## 🔑 Environment Setup

### Set MySQL Password
```powershell
# In application.properties
spring.datasource.password=YOUR_PASSWORD
```

### Set Stripe Key
```powershell
# In application.properties
stripe.api.key=sk_test_YOUR_STRIPE_KEY
```

---

## 🎯 Common Workflows

### 1. Adding a New Entity
```powershell
# 1. Create entity class (e.g., Category.java)
# 2. Create repository interface (CategoryRepository.java)
# 3. Run application (JPA will create table)
mvn spring-boot:run
# 4. Check database
mysql -u root -p
USE revticketsnew;
SHOW TABLES;
```

### 2. Adding a New API Endpoint
```powershell
# 1. Create DTO (if needed)
# 2. Add method in Service
# 3. Add endpoint in Controller
# 4. Restart backend
mvn spring-boot:run
# 5. Test with Postman
```

### 3. Adding a New Angular Component
```powershell
# 1. Generate component
ng g c components/movie-card

# 2. Add to routing (if needed)
# Edit app-routing.module.ts

# 3. Import in parent component
# Edit parent.component.ts

# 4. Use in template
# <app-movie-card></app-movie-card>
```

---

## 📊 Monitoring & Logs

### View Backend Logs
```powershell
# Logs appear in console when running
mvn spring-boot:run

# Or check log file (if configured)
type logs/spring-boot.log
```

### Check Database Size
```sql
-- MySQL
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS "Size (MB)"
FROM information_schema.TABLES
WHERE table_schema = "revticketsnew"
ORDER BY (data_length + index_length) DESC;
```

---

## 🧹 Cleanup Commands

### Clear Maven cache
```powershell
mvn dependency:purge-local-repository
```

### Clear Node modules
```powershell
cd frontend
Remove-Item -Recurse -Force node_modules
npm install
```

### Reset Database
```powershell
mysql -u root -p
DROP DATABASE revticketsnew;
source C:/Users/dell/Desktop/revtickets_new/backend/database_schema.sql
```

---

## 🎓 Help Commands

### Maven Help
```powershell
mvn -h
mvn help:describe -Dcmd=compile
```

### Angular Help
```powershell
ng help
ng generate --help
```

### Spring Boot Help
```powershell
mvn spring-boot:help
```

---

## ⚡ Quick Aliases (Optional - Add to PowerShell Profile)

```powershell
# Edit profile
notepad $PROFILE

# Add these aliases:
function backend { cd C:\Users\dell\Desktop\revtickets_new\backend }
function frontend { cd C:\Users\dell\Desktop\revtickets_new\frontend }
function runbe { backend; mvn spring-boot:run }
function runfe { frontend; ng serve }
function mysql-login { mysql -u root -p }
function mongo-login { mongosh }

# Save and reload
. $PROFILE
```

---

## 🔥 Most Used Commands (Copy-Paste Ready)

```powershell
# Start all services
net start MySQL80; net start MongoDB

# Backend - clean build and run
cd C:\Users\dell\Desktop\revtickets_new\backend; mvn clean install; mvn spring-boot:run

# Frontend - run dev server
cd C:\Users\dell\Desktop\revtickets_new\frontend; ng serve

# Quick database check
mysql -u root -p -e "USE revticketsnew; SHOW TABLES;"

# Test login API
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"email\":\"admin@revtickets.com\",\"password\":\"Admin@123\"}"
```

---

**Save this file for quick reference! 📌**
