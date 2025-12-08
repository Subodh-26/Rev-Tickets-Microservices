@echo off
cd /d "%~dp0"

echo Starting Eureka Server...
start "Eureka" cmd /k "cd microservices\eureka-server && mvn spring-boot:run"
timeout /t 40 /nobreak

echo Starting Core Services...
start "User Service" cmd /k "cd microservices\user-service && mvn spring-boot:run"
start "Movie Service" cmd /k "cd microservices\movie-service && mvn spring-boot:run"
start "Venue Service" cmd /k "cd microservices\venue-service && mvn spring-boot:run"
timeout /t 20 /nobreak

echo Starting Booking and Payment Services...
start "Booking Service" cmd /k "cd microservices\booking-service && mvn spring-boot:run"
start "Payment Service" cmd /k "cd microservices\payment-service && mvn spring-boot:run"
timeout /t 20 /nobreak

echo Starting API Gateway...
start "API Gateway" cmd /k "cd microservices\api-gateway && mvn spring-boot:run"
timeout /t 15 /nobreak

echo Starting Frontend...
start "Angular" cmd /k "cd frontend && npm start"

echo All services started!
echo Eureka: http://localhost:8761
echo API Gateway: http://localhost:8080
echo Frontend: http://localhost:4200
