pipeline {
    agent any
    
    environment {
        DOCKER_HUB_USER = 'subodhxo'
        DOCKER_HUB_PASS = 'Subodh@2002'
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/subodhxo/revtickets.git'
            }
        }
        
        stage('Build JARs') {
            steps {
                bat 'cd microservices\\eureka-server && mvn clean package -DskipTests'
                bat 'cd microservices\\api-gateway && mvn clean package -DskipTests'
                bat 'cd microservices\\user-service && mvn clean package -DskipTests'
                bat 'cd microservices\\movie-service && mvn clean package -DskipTests'
                bat 'cd microservices\\venue-service && mvn clean package -DskipTests'
                bat 'cd microservices\\booking-service && mvn clean package -DskipTests'
                bat 'cd microservices\\payment-service && mvn clean package -DskipTests'
            }
        }
        
        stage('Build Frontend') {
            steps {
                bat 'cd frontend && npm install && npm run build'
            }
        }
        
        stage('Build Docker Images') {
            steps {
                bat 'docker build -t subodhxo/revtickets-eureka:latest .\\microservices\\eureka-server'
                bat 'docker build -t subodhxo/revtickets-gateway:latest .\\microservices\\api-gateway'
                bat 'docker build -t subodhxo/revtickets-user:latest .\\microservices\\user-service'
                bat 'docker build -t subodhxo/revtickets-movie:latest .\\microservices\\movie-service'
                bat 'docker build -t subodhxo/revtickets-venue:latest .\\microservices\\venue-service'
                bat 'docker build -t subodhxo/revtickets-booking:latest .\\microservices\\booking-service'
                bat 'docker build -t subodhxo/revtickets-payment:latest .\\microservices\\payment-service'
                bat 'docker build -t subodhxo/revtickets-frontend:latest .\\frontend'
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                bat 'docker login -u subodhxo -p Subodh@2002'
                bat 'docker push subodhxo/revtickets-eureka:latest'
                bat 'docker push subodhxo/revtickets-gateway:latest'
                bat 'docker push subodhxo/revtickets-user:latest'
                bat 'docker push subodhxo/revtickets-movie:latest'
                bat 'docker push subodhxo/revtickets-venue:latest'
                bat 'docker push subodhxo/revtickets-booking:latest'
                bat 'docker push subodhxo/revtickets-payment:latest'
                bat 'docker push subodhxo/revtickets-frontend:latest'
            }
        }
    }
    
    post {
        always {
            bat 'docker logout'
        }
    }
}
