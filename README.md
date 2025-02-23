# Brokerage Service

## Overview
This is a Spring Boot-based backend service for a brokerage firm, allowing employees to manage stock orders for customers.

## Features
- User Authentication (JWT-based)
- Create, List, and Cancel Stock Orders
- Asset Management
- Admin Order Matching
- Docker Support

## Prerequisites
- Java 17
- Maven
- Docker (optional)

## Setup and Running

### Local Development

#### Build the Project
```bash
mvn clean install
```

#### Run the Application
```bash
mvn spring-boot:run
```

### Docker Deployment

#### Build and Run with Docker
```bash
# Build the Docker image
docker build -t brokerage-api .

# Or use Docker Compose
docker-compose up --build
```

#### Stop Docker Containers
```bash
docker-compose down
```

## API Endpoints

### Authentication
- `POST /api/auth/login`: Login with username and password
- `POST /api/auth/register`: Register a new user

### Orders
- `POST /api/orders/create`: Create a new order
- `GET /api/orders/list`: List orders for a customer
- `DELETE /api/orders/cancel/{orderId}`: Cancel a pending order
- `POST /api/orders/match/{orderId}`: Match an order (Admin only)

### Assets
- `GET /api/assets/list`: List assets for a customer
- `POST /api/assets/create`: Create or update an asset (Admin only)

## Authentication
- Use JWT token in Authorization header: `Bearer {token}`
- Roles: USER, ADMIN

## Database
- H2 in-memory database used
- Console accessible at `/h2-console`

## Testing
```bash
mvn test
```

## Docker Configuration
- Dockerfile for building the application image
- docker-compose.yml for easy deployment
- Supports environment-based configuration

## Notes
- Only PENDING orders can be canceled
- Orders are matched against TRY (Turkish Lira) asset
- Admin can match orders and update asset balances

## Bonus Features
- Customer-level authentication
- Admin order matching endpoint

## Environment Variables
- `JWT_SECRET`: Secret key for JWT token generation
- `JWT_EXPIRATION`: JWT token expiration time
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_DRIVERCLASSNAME`: Database driver class
- `SPRING_JPA_DATABASE-PLATFORM`: Hibernate database platform

## Recommended Production Configurations
- Replace H2 with a persistentw
- Configure secure JWT secret
- Set up proper network security
- Implement comprehensive logging using a centralized logging framework like ELK Stack or Splunk