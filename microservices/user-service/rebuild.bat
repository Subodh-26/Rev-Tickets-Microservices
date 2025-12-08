@echo off
echo Rebuilding User Service...
echo.

cd /d "%~dp0"

echo [1/2] Cleaning and building...
call mvn clean install -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build failed! Check errors above.
    pause
    exit /b 1
)

echo.
echo [2/2] Build successful!
echo.
echo To start the service, run:
echo mvn spring-boot:run
echo.
pause
