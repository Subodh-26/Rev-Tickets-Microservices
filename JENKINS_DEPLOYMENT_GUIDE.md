# Jenkins CI/CD Deployment Guide for RevTickets

## Complete Step-by-Step Setup

---

## Part 1: Prerequisites

### 1.1 AWS EC2 Setup
```bash
# Launch EC2 Instance
- Instance Type: t2.large or t3.large
- OS: Ubuntu 22.04 LTS
- Storage: 30GB+ EBS
- Security Group: Open ports 22, 80, 443, 8080, 8761, 3306, 27017
```

### 1.2 Install Docker on EC2
```bash
# SSH into EC2
ssh -i your-key.pem ubuntu@your-ec2-ip

# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Install Docker Compose
sudo apt install docker-compose -y

# Add user to docker group
sudo usermod -aG docker ubuntu
newgrp docker

# Verify installation
docker --version
docker-compose --version
```

### 1.3 Install Java on EC2
```bash
sudo apt install openjdk-17-jdk -y
java -version
```

---

## Part 2: Jenkins Server Setup

### 2.1 Launch Jenkins EC2 Instance
```bash
# Launch separate EC2 for Jenkins
- Instance Type: t2.medium
- OS: Ubuntu 22.04 LTS
- Storage: 20GB
- Security Group: Open ports 22, 8080
```

### 2.2 Install Jenkins
```bash
# SSH into Jenkins EC2
ssh -i your-key.pem ubuntu@jenkins-ec2-ip

# Install Java
sudo apt update
sudo apt install openjdk-17-jdk -y

# Add Jenkins repository
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee \
  /usr/share/keyrings/jenkins-keyring.asc > /dev/null

echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null

# Install Jenkins
sudo apt update
sudo apt install jenkins -y

# Start Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins

# Get initial admin password
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

### 2.3 Install Docker on Jenkins Server
```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Add jenkins user to docker group
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

### 2.4 Install Maven on Jenkins Server
```bash
sudo apt install maven -y
mvn -version
```

### 2.5 Install Node.js on Jenkins Server
```bash
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install nodejs -y
node --version
npm --version
```

---

## Part 3: Jenkins Configuration

### 3.1 Access Jenkins
```
http://jenkins-ec2-ip:8080
```

### 3.2 Install Required Plugins
Go to: Manage Jenkins → Plugins → Available Plugins

Install:
- Git plugin
- Maven Integration plugin
- Docker Pipeline plugin
- SSH Agent plugin
- Credentials Binding plugin
- Pipeline plugin

### 3.3 Configure Credentials

#### Docker Hub Credentials
1. Go to: Manage Jenkins → Credentials → System → Global credentials
2. Click "Add Credentials"
3. Kind: Username with password
4. ID: `docker-hub-credentials`
5. Username: Your Docker Hub username
6. Password: Your Docker Hub password

#### EC2 SSH Key
1. Add Credentials
2. Kind: SSH Username with private key
3. ID: `ec2-ssh-key`
4. Username: `ubuntu`
5. Private Key: Paste your EC2 private key (.pem file content)

#### EC2 Host
1. Add Credentials
2. Kind: Secret text
3. ID: `ec2-host`
4. Secret: Your EC2 public IP or domain

#### MySQL Password
1. Add Credentials
2. Kind: Secret text
3. ID: `mysql-root-password`
4. Secret: Your MySQL root password

#### Stripe API Key
1. Add Credentials
2. Kind: Secret text
3. ID: `stripe-api-key`
4. Secret: Your Stripe API key

---

## Part 4: Prepare Application Server (EC2)

### 4.1 Create Project Directory
```bash
# SSH into application EC2
ssh -i your-key.pem ubuntu@your-ec2-ip

# Create directory
mkdir -p /home/ubuntu/revtickets
cd /home/ubuntu/revtickets
```

### 4.2 Copy docker-compose-production.yml
```bash
# On your local machine, copy the file to EC2
scp -i your-key.pem docker-compose-production.yml ubuntu@your-ec2-ip:/home/ubuntu/revtickets/
```

### 4.3 Update docker-compose-production.yml
```bash
# Edit the file on EC2
nano /home/ubuntu/revtickets/docker-compose-production.yml

# Update image names to match your Docker Hub repo
# Replace 'revtickets-' with 'yourusername/revtickets-'
```

---

## Part 5: Create Jenkins Pipeline

### 5.1 Create New Pipeline Job
1. Jenkins Dashboard → New Item
2. Name: `RevTickets-Pipeline`
3. Type: Pipeline
4. Click OK

### 5.2 Configure Pipeline
1. Under "Pipeline" section
2. Definition: Pipeline script from SCM
3. SCM: Git
4. Repository URL: Your GitHub repo URL
5. Branch: */main
6. Script Path: `Jenkinsfile-Complete`
7. Save

### 5.3 Update Jenkinsfile
Edit `Jenkinsfile-Complete` and update:
```groovy
DOCKER_HUB_REPO = 'yourusername'  // Your Docker Hub username
git url: 'https://github.com/yourusername/revtickets.git'  // Your repo
```

---

## Part 6: Push Code to GitHub

### 6.1 Initialize Git Repository
```bash
cd d:\Rev_TicketsFi\RevTickets

# Initialize git (if not already)
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit with Docker and Jenkins setup"
```

### 6.2 Create GitHub Repository
1. Go to GitHub.com
2. Create new repository: `revtickets`
3. Don't initialize with README

### 6.3 Push to GitHub
```bash
git remote add origin https://github.com/yourusername/revtickets.git
git branch -M main
git push -u origin main
```

---

## Part 7: Configure GitHub Webhook (Optional)

### 7.1 In GitHub Repository
1. Go to Settings → Webhooks → Add webhook
2. Payload URL: `http://jenkins-ec2-ip:8080/github-webhook/`
3. Content type: application/json
4. Events: Just the push event
5. Active: ✓
6. Add webhook

### 7.2 In Jenkins Job
1. Configure job
2. Build Triggers → GitHub hook trigger for GITScm polling
3. Save

---

## Part 8: Run the Pipeline

### 8.1 Manual Trigger
1. Go to Jenkins Dashboard
2. Click on `RevTickets-Pipeline`
3. Click "Build Now"

### 8.2 Monitor Build
1. Click on build number (e.g., #1)
2. Click "Console Output"
3. Watch the logs

### 8.3 Pipeline Stages
```
1. Checkout - Pull code from GitHub
2. Build Microservices - Maven builds all services
3. Build Frontend - npm build Angular app
4. Run Tests - Execute unit tests
5. Build Docker Images - Create 8 Docker images
6. Push to Docker Hub - Upload images
7. Deploy to EC2 - SSH and docker-compose up
8. Health Check - Verify services are running
```

---

## Part 9: Verify Deployment

### 9.1 Check Running Containers
```bash
# SSH into EC2
ssh -i your-key.pem ubuntu@your-ec2-ip

# Check containers
docker ps

# Should see 10 containers:
# - mysql
# - mongodb
# - eureka-server
# - api-gateway
# - user-service
# - movie-service
# - venue-service
# - booking-service
# - payment-service
# - frontend
```

### 9.2 Check Logs
```bash
# View logs for specific service
docker logs revtickets-eureka
docker logs revtickets-gateway
docker logs revtickets-frontend

# Follow logs
docker logs -f revtickets-gateway
```

### 9.3 Access Application
```
Frontend: http://your-ec2-ip
API Gateway: http://your-ec2-ip:8080
Eureka Dashboard: http://your-ec2-ip:8761
```

---

## Part 10: Troubleshooting

### 10.1 Build Fails at Maven Stage
```bash
# Check Java version on Jenkins
java -version

# Check Maven version
mvn -version

# Verify pom.xml files exist
```

### 10.2 Docker Build Fails
```bash
# Check Docker is running on Jenkins
docker ps

# Check jenkins user has docker permissions
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

### 10.3 Deployment Fails
```bash
# Check SSH connection from Jenkins to EC2
ssh -i /path/to/key ubuntu@ec2-ip

# Check docker-compose file exists on EC2
ls -la /home/ubuntu/revtickets/

# Check EC2 has enough resources
free -h
df -h
```

### 10.4 Services Not Starting
```bash
# Check container logs
docker logs revtickets-eureka

# Check if ports are available
netstat -tulpn | grep 8761

# Restart specific service
docker-compose -f docker-compose-production.yml restart eureka-server
```

### 10.5 Database Connection Issues
```bash
# Check MySQL is running
docker logs revtickets-mysql

# Check MongoDB is running
docker logs revtickets-mongodb

# Test connection
docker exec -it revtickets-mysql mysql -uroot -p
```

---

## Part 11: Environment Variables

### 11.1 Create .env File on EC2
```bash
# Create .env file
nano /home/ubuntu/revtickets/.env

# Add variables
MYSQL_ROOT_PASSWORD=your_secure_password
STRIPE_API_KEY=your_stripe_key
```

### 11.2 Update docker-compose
```yaml
# docker-compose-production.yml already uses ${VARIABLE} syntax
# It will automatically read from .env file
```

---

## Part 12: Continuous Deployment Flow

### 12.1 Developer Workflow
```bash
# 1. Make code changes locally
# 2. Commit changes
git add .
git commit -m "Feature: Add new functionality"

# 3. Push to GitHub
git push origin main

# 4. Jenkins automatically triggers (if webhook configured)
# 5. Pipeline runs all stages
# 6. Application deployed to EC2
# 7. Verify deployment
```

### 12.2 Rollback Strategy
```bash
# If deployment fails, rollback on EC2
ssh ubuntu@ec2-ip
cd /home/ubuntu/revtickets

# Pull previous version
docker-compose -f docker-compose-production.yml down
docker pull yourusername/revtickets-gateway:previous-tag
docker-compose -f docker-compose-production.yml up -d
```

---

## Part 13: Monitoring

### 13.1 Check Service Health
```bash
# Eureka Dashboard
curl http://ec2-ip:8761/actuator/health

# API Gateway
curl http://ec2-ip:8080/actuator/health

# All registered services visible at
http://ec2-ip:8761
```

### 13.2 Resource Monitoring
```bash
# Check CPU and Memory
docker stats

# Check disk usage
df -h

# Check logs size
du -sh /var/lib/docker/containers/*
```

---

## Part 14: Security Best Practices

### 14.1 Secure Jenkins
```bash
# Enable HTTPS
# Use strong passwords
# Limit user permissions
# Regular updates
```

### 14.2 Secure EC2
```bash
# Use security groups properly
# Only open required ports
# Use IAM roles
# Enable CloudWatch monitoring
# Regular security patches
```

### 14.3 Secure Docker Images
```bash
# Scan images for vulnerabilities
docker scan revtickets-gateway:latest

# Use official base images
# Keep images updated
# Don't store secrets in images
```

---

## Part 15: Scaling

### 15.1 Horizontal Scaling
```yaml
# Update docker-compose-production.yml
services:
  user-service:
    deploy:
      replicas: 3
```

### 15.2 Load Balancer
```bash
# Add AWS Application Load Balancer
# Point to multiple EC2 instances
# Configure health checks
```

---

## Summary

**You now have:**
✅ Dockerfiles for all 7 microservices + frontend
✅ Production docker-compose with MySQL, MongoDB, Eureka
✅ Complete Jenkins pipeline
✅ Automated CI/CD from Git push to EC2 deployment
✅ Separate containers for each service
✅ Health checks and monitoring
✅ Rollback capability

**Access Points:**
- Frontend: http://ec2-ip
- API Gateway: http://ec2-ip:8080
- Eureka: http://ec2-ip:8761
- Jenkins: http://jenkins-ip:8080

**Next Steps:**
1. Push code to GitHub
2. Configure Jenkins credentials
3. Run pipeline
4. Monitor deployment
5. Access application
