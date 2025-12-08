@echo off
cd /d "%~dp0\microservices"

start "Movie Service" cmd /k "cd movie-service && mvn spring-boot:run"
start "Venue Service" cmd /k "cd venue-service && mvn spring-boot:run"
timeout /t 15 /nobreak

start "Booking Service" cmd /k "cd booking-service && mvn spring-boot:run"
start "Payment Service" cmd /k "cd payment-service && mvn spring-boot:run"
timeout /t 15 /nobreak

start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run"

echo All services starting...
