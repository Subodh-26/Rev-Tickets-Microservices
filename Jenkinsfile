pipeline {
    agent any
    
    environment {
        BACKEND_IMAGE = 'revtickets-backend'
        FRONTEND_IMAGE = 'revtickets-frontend'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Subodh-26/RevTicketsF.git'
            }
        }
        
        stage('Build Backend') {
            steps {
                dir('backend') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Build Docker Images') {
            steps {
                script {
                    def backendSuccess = false
                    def frontendSuccess = false
                    
                    // Build Backend
                    try {
                        dir('backend') {
                            bat """
                                docker build -t ${BACKEND_IMAGE}:${IMAGE_TAG} .
                                docker tag ${BACKEND_IMAGE}:${IMAGE_TAG} ${BACKEND_IMAGE}:latest
                            """
                        }
                        backendSuccess = true
                        echo '✓ Backend image built successfully'
                    } catch (Exception e) {
                        echo "✗ Backend build failed: ${e.message}"
                    }
                    
                    // Build Frontend
                    try {
                        dir('frontend') {
                            bat """
                                docker build -t ${FRONTEND_IMAGE}:${IMAGE_TAG} .
                                docker tag ${FRONTEND_IMAGE}:${IMAGE_TAG} ${FRONTEND_IMAGE}:latest
                            """
                        }
                        frontendSuccess = true
                        echo '✓ Frontend image built successfully'
                    } catch (Exception e) {
                        echo "✗ Frontend build failed: ${e.message}"
                    }
                    
                    // Summary
                    if (!backendSuccess && !frontendSuccess) {
                        error('Both Docker builds failed - check network connectivity')
                    } else if (!backendSuccess || !frontendSuccess) {
                        echo 'WARNING: One or more Docker builds failed'
                    }
                }
            }
        }
        

    }
    
    post {
        success {
            echo 'Build successful! Docker images created.'
        }
        failure {
            echo 'Build failed!'
        }
        always {
            bat 'docker system prune -f || exit 0'
        }
    }
}
