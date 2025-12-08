import os
import shutil

# Service configurations
services = {
    'movie-service': {
        'port': 8082,
        'db': 'revtickets_movie_db',
        'package': 'movieservice',
        'entities': ['Movie', 'Event']
    },
    'venue-service': {
        'port': 8083,
        'db': 'revtickets_venue_db',
        'package': 'venueservice',
        'entities': ['Venue', 'Screen']
    },
    'booking-service': {
        'port': 8084,
        'db': 'revtickets_booking_db',
        'package': 'bookingservice',
        'entities': ['Booking', 'BookingSeat', 'Seat', 'Show']
    },
    'payment-service': {
        'port': 8085,
        'db': 'revtickets_payment_db',
        'package': 'paymentservice',
        'entities': ['Payment']
    }
}

def create_directory_structure(service_name, package_name):
    base_path = f"{service_name}/src/main"
    java_path = f"{base_path}/java/com/revature/{package_name}"
    
    dirs = [
        f"{java_path}/entity",
        f"{java_path}/repository",
        f"{java_path}/service",
        f"{java_path}/controller",
        f"{java_path}/dto",
        f"{java_path}/config",
        f"{base_path}/resources"
    ]
    
    for dir_path in dirs:
        os.makedirs(dir_path, exist_ok=True)
    
    return java_path

def create_pom_xml(service_name):
    artifact_id = service_name
    pom_content = f'''<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    <groupId>com.revature</groupId>
    <artifactId>{artifact_id}</artifactId>
    <version>1.0.0</version>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>2023.0.0</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>'''
    
    with open(f"{service_name}/pom.xml", 'w') as f:
        f.write(pom_content)

def create_application_yml(service_name, config):
    app_name = service_name
    port = config['port']
    db_name = config['db']
    
    yml_content = f'''server:
  port: {port}

spring:
  application:
    name: {app_name}
  datasource:
    url: jdbc:mysql://localhost:3306/{db_name}
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
'''
    
    with open(f"{service_name}/src/main/resources/application.yml", 'w') as f:
        f.write(yml_content)

def create_main_application(service_name, package_name):
    class_name = ''.join(word.capitalize() for word in service_name.split('-'))
    
    content = f'''package com.revature.{package_name};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class {class_name}Application {{
    public static void main(String[] args) {{
        SpringApplication.run({class_name}Application.class, args);
    }}
}}
'''
    
    path = f"{service_name}/src/main/java/com/revature/{package_name}/{class_name}Application.java"
    with open(path, 'w') as f:
        f.write(content)

def create_api_response_dto(service_name, package_name):
    content = f'''package com.revature.{package_name}.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {{
    private boolean success;
    private String message;
    private T data;
}}
'''
    
    path = f"{service_name}/src/main/java/com/revature/{package_name}/dto/ApiResponse.java"
    with open(path, 'w') as f:
        f.write(content)

# Generate all services
for service_name, config in services.items():
    print(f"Generating {service_name}...")
    
    package_name = config['package']
    
    # Create directory structure
    create_directory_structure(service_name, package_name)
    
    # Create pom.xml
    create_pom_xml(service_name)
    
    # Create application.yml
    create_application_yml(service_name, config)
    
    # Create main application class
    create_main_application(service_name, package_name)
    
    # Create ApiResponse DTO
    create_api_response_dto(service_name, package_name)
    
    print(f"✅ {service_name} generated successfully!")

print("\n✅ All services generated! Now copying entities from monolith...")
