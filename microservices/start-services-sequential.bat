@echo off
echo Starting all microservices sequentially...
echo.

echo Starting User Service on port 8081...
cd user-service
start "User-Service-8081" cmd /k "mvn spring-boot:run"
cd ..
timeout /t 20

echo Starting Movie Service on port 8082...
cd movie-service
start "Movie-Service-8082" cmd /k "mvn spring-boot:run"
cd ..
timeout /t 20

echo Starting Venue Service on port 8083...
cd venue-service
start "Venue-Service-8083" cmd /k "mvn spring-boot:run"
cd ..
timeout /t 20

echo Starting Booking Service on port 8084...
cd booking-service
start "Booking-Service-8084" cmd /k "mvn spring-boot:run"
cd ..
timeout /t 20

echo Starting Payment Service on port 8085...
cd payment-service
start "Payment-Service-8085" cmd /k "mvn spring-boot:run"
cd ..

echo.
echo All services started!
echo Check individual windows for startup status.
pause
