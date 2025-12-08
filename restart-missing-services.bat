@echo off
cd /d "%~dp0"

echo Starting Venue Service...
start "Venue Service" cmd /k "cd microservices\venue-service && mvn spring-boot:run"
timeout /t 15 /nobreak

echo Starting Booking Service...
start "Booking Service" cmd /k "cd microservices\booking-service && mvn spring-boot:run"

echo Services starting...
