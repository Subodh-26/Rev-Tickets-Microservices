# RevTickets - Quick Reference Card

## Docker Commands

### Build All Images Locally
```bash
# Windows
build-and-deploy-local.bat

# Linux/Mac
chmod +x build-and-deploy-local.sh
./build-and-deploy-local.sh
```

### Start All Services
```bash
docker-compose -f docker-compose-production.yml up -d
```

### Stop All Services
```bash
docker-compose -f docker-compose-production.yml down
```

### View Logs
```bash
# All services
docker-compose -f docker-compose-production.yml logs -f

# Specific service
docker logs -f revtickets-eureka
docker logs -f revtickets-gateway
docker logs -f revtickets-user
```

### Check Running Containers
```bash
docker ps
```

### Restart Specific Service
```bash
docker-compose -f docker-compose-production.yml restart eureka-server
```

---

## Service Ports

| Service | Port | URL |
|---------|------|-----|
| Frontend | 80 | http://localhost |
| API Gateway | 8080 | http://localhost:8080 |
| Eureka Server | 8761 | http://localhost:8761 |
| User Service | 8081 | http://localhost:8081 |
| Movie Service | 8082 | http://localhost:8082 |
| Venue Service | 8083 | http://localhost:8083 |
| Booking Service | 8084 | http://localhost:8084 |
| Payment Service | 8085 | http://localhost:8085 |
| MySQL | 3306 | localhost:3306 |
| MongoDB | 27017 | localhost:27017 |

---

## Jenkins Pipeline Stages

1. **Checkout** - Pull code from Git
2. **Build Microservices** - Maven build all services
3. **Build Frontend** - npm build Angular
4. **Run Tests** - Execute unit tests
5. **Build Docker Images** - Create 8 images
6. **Push to Docker Hub** - Upload images
7. **Deploy to EC2** - SSH and deploy
8. **Health Check** - Verify services

---

## Jenkins Credentials Required

| ID | Type | Description |
|----|------|-------------|
| docker-hub-credentials | Username/Password | Docker Hub login |
| ec2-ssh-key | SSH Private Key | EC2 access key |
| ec2-host | Secret Text | EC2 IP address |
| mysql-root-password | Secret Text | MySQL password |
| stripe-api-key | Secret Text | Stripe API key |

---

## EC2 Setup Commands

```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Install Docker Compose
sudo apt install docker-compose -y

# Add user to docker group
sudo usermod -aG docker ubuntu
newgrp docker

# Create project directory
mkdir -p /home/ubuntu/revtickets
cd /home/ubuntu/revtickets
```

---

## Troubleshooting

### Service Won't Start
```bash
# Check logs
docker logs revtickets-servicename

# Restart service
docker-compose -f docker-compose-production.yml restart servicename

# Rebuild and restart
docker-compose -f docker-compose-production.yml up -d --build servicename
```

### Database Connection Issues
```bash
# Check MySQL
docker exec -it revtickets-mysql mysql -uroot -p

# Check MongoDB
docker exec -it revtickets-mongodb mongosh
```

### Port Already in Use
```bash
# Find process using port
netstat -ano | findstr :8080

# Kill process (Windows)
taskkill /PID <pid> /F

# Kill process (Linux)
kill -9 <pid>
```

### Clean Everything
```bash
# Stop and remove all containers
docker-compose -f docker-compose-production.yml down -v

# Remove all images
docker rmi $(docker images -q)

# Clean system
docker system prune -a
```

---

## Git Workflow

```bash
# Make changes
git add .
git commit -m "Your message"
git push origin main

# Jenkins automatically builds and deploys (if webhook configured)
```

---

## Health Check URLs

```bash
# Eureka
curl http://localhost:8761/actuator/health

# API Gateway
curl http://localhost:8080/actuator/health

# User Service
curl http://localhost:8081/actuator/health
```

---

## Environment Variables

Create `.env` file:
```env
MYSQL_ROOT_PASSWORD=your_password
STRIPE_API_KEY=your_stripe_key
```

---

## Docker Hub Push (Manual)

```bash
# Login
docker login

# Tag images
docker tag revtickets-eureka:latest yourusername/revtickets-eureka:latest

# Push
docker push yourusername/revtickets-eureka:latest
```

---

## Backup Database

```bash
# MySQL backup
docker exec revtickets-mysql mysqldump -uroot -p revtickets > backup.sql

# MongoDB backup
docker exec revtickets-mongodb mongodump --out /backup
```

---

## Scale Services

```bash
# Scale specific service
docker-compose -f docker-compose-production.yml up -d --scale user-service=3
```

---

## Monitor Resources

```bash
# Real-time stats
docker stats

# Disk usage
docker system df

# Container sizes
docker ps -s
```
