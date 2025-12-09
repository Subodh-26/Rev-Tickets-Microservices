# Fix Docker Hub Connection Issue

## Step 1: Restart Docker Desktop
1. Right-click Docker icon in system tray
2. Click "Quit Docker Desktop"
3. Wait 30 seconds
4. Open Docker Desktop from Start menu
5. Wait until whale icon is steady

## Step 2: Reset Docker Network
Open PowerShell as Administrator:

```powershell
# Stop Docker
net stop com.docker.service

# Restart network
ipconfig /flushdns
netsh winsock reset

# Start Docker
net start com.docker.service
```

## Step 3: Configure Docker Proxy (if behind firewall)
1. Open Docker Desktop
2. Settings → Resources → Proxies
3. If you're behind corporate proxy, add proxy settings
4. Otherwise, ensure "Manual proxy configuration" is OFF

## Step 4: Test Connection
```cmd
docker login -u subodhxo -p Subodh@26
```

## Step 5: If Still Fails - Use Alternative Method

### Option A: Push from Local Command Line
```cmd
cd d:\Rev_TicketsFi\RevTickets

# Build images
docker build -t subodhxo/revtickets-eureka:latest .\microservices\eureka-server
docker build -t subodhxo/revtickets-gateway:latest .\microservices\api-gateway
docker build -t subodhxo/revtickets-user:latest .\microservices\user-service
docker build -t subodhxo/revtickets-movie:latest .\microservices\movie-service
docker build -t subodhxo/revtickets-venue:latest .\microservices\venue-service
docker build -t subodhxo/revtickets-booking:latest .\microservices\booking-service
docker build -t subodhxo/revtickets-payment:latest .\microservices\payment-service
docker build -t subodhxo/revtickets-frontend:latest .\frontend

# Login
docker login -u subodhxo -p Subodh@26

# Push all
docker push subodhxo/revtickets-eureka:latest
docker push subodhxo/revtickets-gateway:latest
docker push subodhxo/revtickets-user:latest
docker push subodhxo/revtickets-movie:latest
docker push subodhxo/revtickets-venue:latest
docker push subodhxo/revtickets-booking:latest
docker push subodhxo/revtickets-payment:latest
docker push subodhxo/revtickets-frontend:latest
```

### Option B: Skip Jenkins, Deploy Directly to EC2
1. SSH to EC2
2. Pull images directly on EC2
3. Run docker-compose

```bash
ssh -i your-key.pem ubuntu@your-ec2-ip

# On EC2
cd /home/ubuntu/revtickets
docker login -u subodhxo -p Subodh@26
docker-compose -f docker-compose-production.yml pull
docker-compose -f docker-compose-production.yml up -d
```

## Step 6: Check Docker Hub Website
Go to: https://hub.docker.com/u/subodhxo

See which images are already pushed. You only need to push the missing ones.
