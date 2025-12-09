# RevTickets - Movie Ticket Booking Platform
## 15 Slide Presentation

---

## Slide 1: Title
**RevTickets**
Movie Ticket Booking Platform
Microservices Architecture | Spring Cloud | Angular 18

---

## Slide 2: Project Overview
**Full-stack movie ticket booking application**
- Real-time seat selection with WebSocket
- Secure payment integration (Stripe)
- Microservices architecture
- Responsive Angular frontend
- Deployed on AWS EC2 via Jenkins CI/CD

---

## Slide 3: Technology Stack
**Backend:**
- Spring Boot 3.2.0 | Spring Cloud 2023.0.0
- MySQL 8.0 + MongoDB
- WebSocket (SockJS + STOMP)

**Frontend:**
- Angular 18.2.0 | TypeScript 5.5.4
- Tailwind CSS | RxJS

**DevOps:**
- Docker | Jenkins | AWS EC2

---

## Slide 4: Microservices Architecture
```
Angular Frontend (:4200)
        ↓
API Gateway (:8080) ← Eureka Server (:8761)
        ↓
┌───────┴───────┬──────────┬──────────┬──────────┐
User      Movie     Venue    Booking    Payment
Service   Service   Service  Service    Service
:8081     :8082     :8083    :8084      :8085
```

**7 Services:** Eureka, Gateway, User, Movie, Venue, Booking, Payment

---

## Slide 5: Key Features
- User authentication & authorization (JWT)
- Movie browsing with search
- Theater & screen management
- **Real-time seat availability** (WebSocket)
- Interactive seat selection
- Stripe payment processing
- Booking history & management
- Admin panel

---

## Slide 6: Database Architecture
**MySQL (Relational):**
- Users, bookings, transactions
- Venues, screens, shows, seats

**MongoDB (NoSQL):**
- Movies catalog
- Events data

**Hybrid approach** for optimal performance

---

## Slide 7: Real-Time Communication
**WebSocket Implementation:**
- Technology: SockJS + STOMP
- Endpoint: `/ws`
- Topic: `/topic/seats/{showId}`
- **Purpose:** Instant seat status updates
- **Benefit:** Prevents double booking

---

## Slide 8: API Gateway Pattern
**Centralized routing (Port 8080):**
- `/api/users/**` → User Service
- `/api/movies/**` → Movie Service
- `/api/venues/**` → Venue Service
- `/api/bookings/**` → Booking Service
- `/api/payments/**` → Payment Service

**Features:** Load balancing, service discovery, fault tolerance

---

## Slide 9: CI/CD Pipeline - Jenkins
**Pipeline Stages:**
```
1. Checkout → Git repository
2. Build → Maven (services) + npm (frontend)
3. Test → Unit & integration tests
4. Docker Build → Create images for all services
5. Push → Docker Hub registry
6. Deploy → SSH to EC2 & docker-compose up
```

**Trigger:** Automated on Git push

---

## Slide 10: Jenkins Pipeline Code
```groovy
pipeline {
    stages {
        stage('Build Services') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('Build Frontend') {
            steps {
                sh 'npm install && npm run build'
            }
        }
        stage('Docker Images') {
            steps {
                sh 'docker build -t revtickets-eureka ./eureka-server'
                sh 'docker build -t revtickets-gateway ./api-gateway'
                // Build all service images
            }
        }
        stage('Deploy to EC2') {
            steps {
                sshagent(['ec2-key']) {
                    sh 'ssh ubuntu@ec2-ip "cd revtickets && docker-compose up -d"'
                }
            }
        }
    }
}
```

---

## Slide 11: AWS EC2 Deployment Setup
**EC2 Configuration:**
- Instance: t2.large (Ubuntu 22.04)
- Storage: 30GB EBS
- Security Group Ports: 22, 80, 443, 8080, 8761

**Installed Software:**
- Docker & Docker Compose
- Java 17
- MySQL & MongoDB containers

**Deployment:** docker-compose.yml with all services

---

## Slide 12: Deployment Architecture
```
Developer → Git Push
    ↓
GitHub Repository
    ↓
Jenkins (Webhook)
    ├─ Build & Test
    ├─ Create Docker Images
    └─ Push to Registry
        ↓
AWS EC2 Instance
    ├─ Pull Images
    ├─ docker-compose down
    └─ docker-compose up -d
        ↓
Application Live
```

---

## Slide 13: Docker Compose Production
```yaml
services:
  mysql:
    image: mysql:8.0
    volumes: [mysql-data:/var/lib/mysql]
  
  mongodb:
    image: mongo:latest
  
  eureka:
    image: revtickets-eureka:latest
    ports: ["8761:8761"]
  
  gateway:
    image: revtickets-gateway:latest
    ports: ["8080:8080"]
  
  # user, movie, venue, booking, payment services
  
  frontend:
    image: revtickets-frontend:latest
    ports: ["80:80"]
```

---

## Slide 14: Security & Monitoring
**Security:**
- JWT authentication
- BCrypt password encryption
- HTTPS/SSL certificates
- AWS Security Groups
- Environment variables for secrets

**Monitoring:**
- Eureka Dashboard (service health)
- Spring Boot Actuator
- Docker health checks
- AWS CloudWatch

---

## Slide 15: Demo & Conclusion
**Demo Flow:**
1. User login → Browse movies
2. Select theater & show
3. Real-time seat selection
4. Payment → Booking confirmation

**Achievements:**
- Scalable microservices architecture
- Real-time user experience
- Automated CI/CD deployment
- Production-ready on AWS

**Q&A**
