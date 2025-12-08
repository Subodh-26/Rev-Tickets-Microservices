@echo off
echo Building all microservices...

cd movie-service
call mvn clean install -DskipTests
cd ..

cd venue-service
call mvn clean install -DskipTests
cd ..

cd booking-service
call mvn clean install -DskipTests
cd ..

cd payment-service
call mvn clean install -DskipTests
cd ..

echo All services built successfully!
