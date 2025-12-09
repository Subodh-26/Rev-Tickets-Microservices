# RevTickets - Movie Ticket Booking Platform
## Presentation Outline

---

## Slide 1: Title Slide
**RevTickets**
Movie Ticket Booking Platform
Microservices Architecture with Spring Cloud

---

## Slide 2: Project Overview
- **Full-stack movie ticket booking application**
- **Real-time seat selection and booking**
- **Payment integration with Stripe**
- **Microservices-based architecture**
- **Responsive Angular frontend**

---

## Slide 3: Technology Stack - Backend
- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0
- **Java**: 17+
- **MySQL**: 8.0 (Relational data)
- **MongoDB**: Latest (Event/Movie data)
- **Maven**: Build tool

---

## Slide 4: Technology Stack - Frontend
- **Angular**: 18.2.0
- **TypeScript**: 5.5.4
- **Tailwind CSS**: 3.3.6
- **RxJS**: 7.8.0
- **Stripe.js**: 2.4.0 (Payment)
- **SockJS + STOMP**: WebSocket for real-time updates

---

## Slide 5: Microservices Architecture
**7 Core Services:**
1. **Eureka Server** (8761) - Service Discovery
2. **API Gateway** (8080) - Entry point, routing
3. **User Service** (8081) - Authentication & user management
4. **Movie Service** (8082) - Movie catalog
5. **Venue Service** (8083) - Theaters & screens
6. **Booking Service** (8084) - Seat booking & shows
7. **Payment Service** (8085) - Payment processing

---

## Slide 6: Architecture Diagram
```
┌─────────────┐
│   Angular   │ :4200
│  Frontend   │
└──────┬──────┘
       │
┌──────▼──────────┐
│  API Gateway    │ :8080
└──────┬──────────┘
       │
┌──────▼──────────┐
│ Eureka Server   │ :8761
│ (Discovery)     │
└──────┬──────────┘
       │
┌──────┴──────────────────────────────┐
│                                     │
▼              ▼              ▼       ▼
User       Movie         Venue    Booking
Service    Service       Service  Service
:8081      :8082         :8083    :8084
                                      │
                                      ▼
                                  Payment
                                  Service
                                  :8085
```

---

## Slide 7: Key Features
- **User Authentication & Authorization**
- **Movie browsing with search & filters**
- **Theater & screen management**
- **Real-time seat availability (WebSocket)**
- **Interactive seat selection**
- **Secure payment processing (Stripe)**
- **Booking history & management**
- **Admin panel for content management**

---

## Slide 8: Database Architecture
**MySQL (Relational)**
- Users & authentication
- Bookings & transactions
- Venues & screens
- Shows & seats

**MongoDB (NoSQL)**
- Movies catalog
- Events data
- Flexible schema for media content

---

## Slide 9: Real-Time Communication
**WebSocket Implementation**
- **Technology**: SockJS + STOMP
- **Purpose**: Real-time seat updates
- **Flow**:
  - User connects to `/ws` endpoint
  - Subscribes to `/topic/seats/{showId}`
  - Receives instant seat status updates
  - Prevents double booking

---

## Slide 10: API Gateway Routing
**Centralized routing through port 8080:**
- `/api/users/**` → User Service
- `/api/movies/**` → Movie Service
- `/api/venues/**` → Venue Service
- `/api/bookings/**` → Booking Service
- `/api/payments/**` → Payment Service
- **Load balancing** with Spring Cloud LoadBalancer

---

## Slide 11: Service Discovery
**Eureka Server Benefits:**
- Dynamic service registration
- Health monitoring
- Load balancing
- Fault tolerance
- No hardcoded service URLs
- Dashboard at `http://localhost:8761`

---

## Slide 12: Frontend Architecture
**Angular 18 Features:**
- Standalone components
- Signal-based state management
- Lazy loading modules
- Route guards for authentication
- Reactive forms
- HTTP interceptors for auth tokens
- Responsive design with Tailwind CSS

---

## Slide 13: Payment Integration
**Stripe Payment Flow:**
1. User selects seats
2. Frontend creates payment intent
3. Payment Service processes via Stripe API
4. Secure card tokenization
5. Payment confirmation
6. Booking confirmation
7. Email notification (optional)

---

## Slide 14: Deployment Architecture - Overview
**Containerization:**
- Docker containers for each service
- Docker Compose for local orchestration
- Dockerfiles for frontend & backend
- MySQL & MongoDB containers

**Target Platform:**
- AWS EC2 instances
- Jenkins CI/CD pipeline
- Automated deployment

---

## Slide 15: Docker Setup
**Services Containerized:**
```yaml
- MySQL (Port 3307)
- MongoDB (Port 27017)
- Backend Services (8080-8085)
- Frontend (Port 80)
```

**Network:**
- Bridge network: `revtickets-network`
- Service-to-service communication
- Health checks for dependencies

---

## Slide 16: CI/CD Pipeline - Jenkins Setup
**Jenkins Installation on EC2:**
1. Launch EC2 instance (t2.medium or larger)
2. Install Java 17
3. Install Jenkins
4. Install required plugins:
   - Git plugin
   - Maven Integration
   - Docker Pipeline
   - AWS EC2 plugin
   - SSH Agent

---

## Slide 17: Jenkins Pipeline - Stage 1
**Source Code Management:**
```groovy
stage('Checkout') {
    steps {
        git branch: 'main',
            url: 'https://github.com/your-repo/revtickets.git'
    }
}
```

**Build Microservices:**
```groovy
stage('Build Services') {
    steps {
        dir('microservices/eureka-server') {
            sh 'mvn clean package -DskipTests'
        }
        // Repeat for all services
    }
}
```

---

## Slide 18: Jenkins Pipeline - Stage 2
**Build Frontend:**
```groovy
stage('Build Frontend') {
    steps {
        dir('frontend') {
            sh 'npm install'
            sh 'npm run build'
        }
    }
}
```

**Run Tests:**
```groovy
stage('Test') {
    steps {
        sh 'mvn test'
    }
}
```

---

## Slide 19: Jenkins Pipeline - Stage 3
**Docker Image Creation:**
```groovy
stage('Build Docker Images') {
    steps {
        sh 'docker build -t revtickets-eureka ./microservices/eureka-server'
        sh 'docker build -t revtickets-gateway ./microservices/api-gateway'
        sh 'docker build -t revtickets-user ./microservices/user-service'
        sh 'docker build -t revtickets-movie ./microservices/movie-service'
        sh 'docker build -t revtickets-venue ./microservices/venue-service'
        sh 'docker build -t revtickets-booking ./microservices/booking-service'
        sh 'docker build -t revtickets-payment ./microservices/payment-service'
        sh 'docker build -t revtickets-frontend ./frontend'
    }
}
```

---

## Slide 20: Jenkins Pipeline - Stage 4
**Push to Docker Registry:**
```groovy
stage('Push Images') {
    steps {
        withCredentials([usernamePassword(
            credentialsId: 'docker-hub',
            usernameVariable: 'DOCKER_USER',
            passwordVariable: 'DOCKER_PASS'
        )]) {
            sh 'docker login -u $DOCKER_USER -p $DOCKER_PASS'
            sh 'docker push revtickets-eureka'
            sh 'docker push revtickets-gateway'
            // Push all images
        }
    }
}
```

---

## Slide 21: AWS EC2 Setup
**Infrastructure Requirements:**
- **EC2 Instance Type**: t2.large or t3.large
- **OS**: Ubuntu 22.04 LTS
- **Storage**: 30GB+ EBS volume
- **Security Group Ports**:
  - 22 (SSH)
  - 80 (HTTP)
  - 443 (HTTPS)
  - 8080 (API Gateway)
  - 8761 (Eureka Dashboard)
  - 3307 (MySQL)
  - 27017 (MongoDB)

---

## Slide 22: EC2 Instance Configuration
**Install Prerequisites:**
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Install Docker Compose
sudo apt install docker-compose -y

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Add user to docker group
sudo usermod -aG docker ubuntu
```

---

## Slide 23: Jenkins Deployment Stage
**Deploy to EC2:**
```groovy
stage('Deploy to EC2') {
    steps {
        sshagent(['ec2-ssh-key']) {
            sh '''
                ssh -o StrictHostKeyChecking=no ubuntu@ec2-ip << EOF
                cd /home/ubuntu/revtickets
                docker-compose pull
                docker-compose down
                docker-compose up -d
                EOF
            '''
        }
    }
}
```

---

## Slide 24: Docker Compose Deployment
**Production docker-compose.yml on EC2:**
```yaml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
    volumes:
      - mysql-data:/var/lib/mysql
    
  eureka:
    image: revtickets-eureka:latest
    ports:
      - "8761:8761"
    
  gateway:
    image: revtickets-gateway:latest
    ports:
      - "8080:8080"
    depends_on:
      - eureka
    
  # All other services...
  
  frontend:
    image: revtickets-frontend:latest
    ports:
      - "80:80"
```

---

## Slide 25: Deployment Flow Diagram
```
Developer → Git Push
    ↓
GitHub Repository
    ↓
Jenkins Webhook Trigger
    ↓
Jenkins Pipeline Execution
    ├─ Checkout Code
    ├─ Build Services (Maven)
    ├─ Build Frontend (npm)
    ├─ Run Tests
    ├─ Build Docker Images
    ├─ Push to Docker Hub
    └─ Deploy to EC2
        ↓
AWS EC2 Instance
    ├─ Pull Images
    ├─ Stop Old Containers
    └─ Start New Containers
        ↓
Application Live
```

---

## Slide 26: Monitoring & Health Checks
**Service Health Monitoring:**
- Eureka Dashboard: Service status
- Spring Boot Actuator endpoints
- Docker container health checks
- AWS CloudWatch for EC2 metrics

**Logging:**
- Centralized logging with ELK stack (optional)
- Application logs in containers
- Jenkins build logs

---

## Slide 27: Security Best Practices
**Implemented Security:**
- JWT token authentication
- Password encryption (BCrypt)
- CORS configuration
- HTTPS with SSL certificates
- Environment variables for secrets
- AWS Security Groups
- Private subnets for databases
- IAM roles for EC2

---

## Slide 28: Scaling Strategy
**Horizontal Scaling:**
- Multiple instances of each microservice
- Load balancing via API Gateway
- Eureka handles service discovery
- Docker Swarm or Kubernetes (future)

**Vertical Scaling:**
- Upgrade EC2 instance types
- Increase database resources
- Optimize JVM heap sizes

---

## Slide 29: Backup & Disaster Recovery
**Database Backups:**
- Automated MySQL dumps
- MongoDB replica sets
- AWS RDS for managed databases (optional)
- S3 for backup storage

**Application Recovery:**
- Docker images versioned
- Rollback capability in Jenkins
- Blue-green deployment strategy

---

## Slide 30: Performance Optimization
**Backend:**
- Connection pooling
- Database indexing
- Caching with Redis (future)
- Async processing

**Frontend:**
- Lazy loading
- AOT compilation
- Image optimization
- CDN for static assets

---

## Slide 31: Testing Strategy
**Backend Testing:**
- Unit tests (JUnit)
- Integration tests
- API testing (Postman collection included)

**Frontend Testing:**
- Component tests
- E2E tests (Cypress/Playwright)

**Load Testing:**
- JMeter for performance testing
- Stress testing booking flow

---

## Slide 32: Cost Optimization - AWS
**EC2 Costs:**
- Use Reserved Instances for production
- Auto-scaling groups
- Spot instances for dev/test

**Storage:**
- EBS volume optimization
- S3 lifecycle policies

**Monitoring:**
- AWS Cost Explorer
- Budget alerts

---

## Slide 33: Future Enhancements
- **Kubernetes deployment** for better orchestration
- **Redis caching** for improved performance
- **Kafka** for event-driven architecture
- **Elasticsearch** for advanced search
- **Mobile app** (React Native)
- **Email notifications** (AWS SES)
- **SMS alerts** (AWS SNS)
- **Analytics dashboard** for admins

---

## Slide 34: Development Workflow
**Local Development:**
```bash
# Start all services
cd RevTickets
start-all.bat

# Access points
- Frontend: http://localhost:4200
- API Gateway: http://localhost:8080
- Eureka: http://localhost:8761
```

**Team Collaboration:**
- Git branching strategy
- Code reviews
- CI/CD automation
- Documentation

---

## Slide 35: Project Metrics
**Codebase:**
- 7 Microservices
- Angular 18 frontend
- RESTful APIs
- WebSocket integration
- Docker containerization

**Infrastructure:**
- Multi-database architecture
- Service discovery
- API Gateway pattern
- Real-time communication

---

## Slide 36: Challenges & Solutions
**Challenge 1: Service Communication**
- Solution: Eureka + API Gateway

**Challenge 2: Real-time Updates**
- Solution: WebSocket with SockJS/STOMP

**Challenge 3: Deployment Complexity**
- Solution: Docker + Jenkins CI/CD

**Challenge 4: Database Management**
- Solution: MySQL + MongoDB hybrid approach

---

## Slide 37: Team & Roles
**Full Stack Development:**
- Backend microservices development
- Frontend Angular development
- Database design & optimization
- DevOps & deployment
- Testing & quality assurance

**Tools Used:**
- IntelliJ IDEA / VS Code
- Postman for API testing
- Git for version control
- Jenkins for CI/CD
- Docker for containerization

---

## Slide 38: Demo Flow
1. **User Registration/Login**
2. **Browse Movies**
3. **Select Theater & Show**
4. **Real-time Seat Selection**
5. **Payment Processing**
6. **Booking Confirmation**
7. **View Booking History**

---

## Slide 39: API Endpoints Summary
**User Service:**
- POST /api/users/register
- POST /api/users/login
- GET /api/users/profile

**Movie Service:**
- GET /api/movies
- GET /api/movies/{id}

**Booking Service:**
- GET /api/bookings/shows/{id}/seats
- POST /api/bookings
- GET /api/bookings/user/{userId}

**Payment Service:**
- POST /api/payments/create-intent
- POST /api/payments/confirm

---

## Slide 40: Conclusion
**RevTickets - Key Takeaways:**
- Modern microservices architecture
- Scalable and maintainable
- Real-time user experience
- Secure payment processing
- Automated CI/CD deployment
- Production-ready on AWS EC2

**Technologies Mastered:**
- Spring Boot & Spring Cloud
- Angular 18
- Docker & Jenkins
- AWS EC2 deployment
- WebSocket communication

---

## Slide 41: Q&A
**Questions?**

**Contact & Resources:**
- GitHub Repository
- API Documentation
- Postman Collection
- Deployment Guide

---

## Slide 42: Thank You
**RevTickets**
Movie Ticket Booking Platform

**Live Demo Available**
