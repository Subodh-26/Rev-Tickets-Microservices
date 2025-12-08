@echo off
echo Stopping all Java processes...
taskkill /F /IM java.exe 2>nul
timeout /t 3 /nobreak

echo Stopping Node processes...
taskkill /F /IM node.exe 2>nul
timeout /t 2 /nobreak

echo Starting services...
cd /d "%~dp0"

start "Eureka" cmd /k "cd microservices\eureka-server && mvn spring-boot:run"
timeout /t 35 /nobreak

start "User Service" cmd /k "cd microservices\user-service && mvn spring-boot:run"
start "Movie Service" cmd /k "cd microservices\movie-service && mvn spring-boot:run"
start "Venue Service" cmd /k "cd microservices\venue-service && mvn spring-boot:run"
timeout /t 15 /nobreak

start "Booking Service" cmd /k "cd microservices\booking-service && mvn spring-boot:run"
start "Payment Service" cmd /k "cd microservices\payment-service && mvn spring-boot:run"
timeout /t 15 /nobreak

start "API Gateway" cmd /k "cd microservices\api-gateway && mvn spring-boot:run"
timeout /t 10 /nobreak

start "Angular" cmd /k "cd frontend && npm start"

echo All services restarted!
