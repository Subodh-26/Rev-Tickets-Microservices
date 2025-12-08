@echo off
echo ========================================
echo Starting RevTickets Full Stack Application
echo ========================================
echo.

cd /d "%~dp0"

echo [1/2] Starting Microservices...
cd microservices
start "Microservices" cmd /k "start-all-services.bat"
cd ..

echo [2/2] Starting Angular Frontend...
timeout /t 5 /nobreak
cd frontend
start "Angular Frontend" cmd /k "npm start"
cd ..

echo.
echo ========================================
echo All services are starting!
echo ========================================
echo.
echo Eureka Dashboard: http://localhost:8761
echo API Gateway: http://localhost:8080
echo Angular Frontend: http://localhost:4200
echo.
echo Wait for all services to start before accessing the application.
pause
