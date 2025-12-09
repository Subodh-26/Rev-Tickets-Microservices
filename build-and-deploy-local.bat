@echo off
echo ========================================
echo Building RevTickets - All Services
echo ========================================

cd /d "%~dp0"

echo [1/8] Building Eureka Server...
cd microservices\eureka-server
call mvn clean package -DskipTests
cd ..\..

echo [2/8] Building API Gateway...
cd microservices\api-gateway
call mvn clean package -DskipTests
cd ..\..

echo [3/8] Building User Service...
cd microservices\user-service
call mvn clean package -DskipTests
cd ..\..

echo [4/8] Building Movie Service...
cd microservices\movie-service
call mvn clean package -DskipTests
cd ..\..

echo [5/8] Building Venue Service...
cd microservices\venue-service
call mvn clean package -DskipTests
cd ..\..

echo [6/8] Building Booking Service...
cd microservices\booking-service
call mvn clean package -DskipTests
cd ..\..

echo [7/8] Building Payment Service...
cd microservices\payment-service
call mvn clean package -DskipTests
cd ..\..

echo [8/8] Building Frontend...
cd frontend
call npm install
call npm run build
cd ..

echo.
echo ========================================
echo All builds completed!
echo ========================================
echo.
echo Now building Docker images...
echo.

docker build -t revtickets-eureka:latest ./microservices/eureka-server
docker build -t revtickets-gateway:latest ./microservices/api-gateway
docker build -t revtickets-user:latest ./microservices/user-service
docker build -t revtickets-movie:latest ./microservices/movie-service
docker build -t revtickets-venue:latest ./microservices/venue-service
docker build -t revtickets-booking:latest ./microservices/booking-service
docker build -t revtickets-payment:latest ./microservices/payment-service
docker build -t revtickets-frontend:latest ./frontend

echo.
echo ========================================
echo Docker images built successfully!
echo ========================================
echo.
echo To start all services, run:
echo docker-compose -f docker-compose-production.yml up -d
echo.
pause
