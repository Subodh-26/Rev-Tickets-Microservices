@echo off
echo Starting all microservices...

cd /d "%~dp0"

start "Eureka Server" cmd /k "cd eureka-server && mvn spring-boot:run"
timeout /t 30 /nobreak

start "User Service" cmd /k "cd user-service && mvn spring-boot:run"
timeout /t 10 /nobreak

start "Movie Service" cmd /k "cd movie-service && mvn spring-boot:run"
timeout /t 10 /nobreak

start "Venue Service" cmd /k "cd venue-service && mvn spring-boot:run"
timeout /t 10 /nobreak

start "Booking Service" cmd /k "cd booking-service && mvn spring-boot:run"
timeout /t 10 /nobreak

start "Payment Service" cmd /k "cd payment-service && mvn spring-boot:run"
timeout /t 10 /nobreak

start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run"

echo All services are starting in separate windows...
echo Wait for all services to register with Eureka before testing.
pause
