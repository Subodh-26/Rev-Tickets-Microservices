@echo off
echo Starting all microservices in parallel...

echo Starting API Gateway on port 8080...
start "API-Gateway-8080" cmd /k "cd api-gateway && mvn spring-boot:run"

echo Starting User Service on port 8081...
start "User-Service-8081" cmd /k "cd user-service && mvn spring-boot:run"

echo Starting Movie Service on port 8082...
start "Movie-Service-8082" cmd /k "cd movie-service && mvn spring-boot:run"

echo Starting Venue Service on port 8083...
start "Venue-Service-8083" cmd /k "cd venue-service && mvn spring-boot:run"

echo Starting Booking Service on port 8084...
start "Booking-Service-8084" cmd /k "cd booking-service && mvn spring-boot:run"

echo Starting Payment Service on port 8085...
start "Payment-Service-8085" cmd /k "cd payment-service && mvn spring-boot:run"

echo.
echo All services are starting in separate windows!
echo Check Eureka dashboard at http://localhost:8761
pause