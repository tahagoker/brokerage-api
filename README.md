# Brokerage Service

## Overview
This is a Spring Boot-based backend service for a brokerage firm, allowing employees to manage stock orders for customers.

## Features
- User Authentication (JWT-based and Admin User/Pass in header)
- Create, List, and Cancel Stock Orders
- Asset Management
- Admin Order Matching
- Docker Support

## API Documentation
Access the full API documentation through Swagger UI at: `http://localhost:8080/swagger-ui/index.html`

## Postman Collection
You can find the Postman collection in the repository. 
Note: Starting from Postman version 10.3.5, direct collection 
sharing via links has been removed. 
Please use the collection file from the repository.

## Authentication Flow
1. **Register a User (POST /auth/register)**
   - Required fields:
     - username
     - password

2. **Login to Get JWT Token (POST /auth/login)**
   - Required fields:
     - username
     - password
   - You'll receive a JWT token in response

3. **Using the API**
   - For all protected endpoints, include the JWT token in the Authorization header
   - Use Bearer authentication scheme
   - Example format can be found in the Postman collection
   - Our put admin user/pass in header. it's default added in postman collection


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

## Architecture Notes
In production-ready brokerage applications, several architectural patterns and approaches are typically implemented to ensure scalability and high availability:
- Message brokers for pub/sub communication
- Microservices architecture
- Saga pattern for distributed transactions
- Outbox pattern for reliable message delivery
- Event-driven architecture

However, this project is implemented as a monolithic service due to:
- Time constraints (weekend project)
- Avoiding over-engineering for the case study
- Simplicity in demonstration


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


## Recommended Production Configurations
- Replace H2 with a persistent database
- Configure secure JWT secret
- Set up proper network security
- Implement comprehensive logging using a centralized logging framework like ELK Stack or Splunk