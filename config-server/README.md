# MedVix Configuration Files

This directory contains centralized configuration files for all MedVix microservices.

## Structure

- `application.yml` - Common configuration shared by all services
- `{service-name}.yml` - Service-specific configuration files

## Services

1. **eureka-server** (Port: 8761) - Service discovery server
2. **config-server** (Port: 8888) - Configuration management server
3. **api-gateway** (Port: 8080) - API Gateway with routing
4. **user-service** (Port: 8081) - User management
5. **medicine-service** (Port: 8082) - Medicine catalog
6. **inventory-service** (Port: 8083) - Inventory management
7. **sales-service** (Port: 8084) - Sales and billing
8. **customer-service** (Port: 8085) - Customer management
9. **dealer-service** (Port: 8086) - Dealer management
10. **notification-service** (Port: 8087) - Notifications
11. **reporting-service** (Port: 8088) - Reports and analytics
12. **billscan-service** (Port: 8089) - Bill scanning and OCR

## Configuration Access

Services can access their configuration using:
- `http://localhost:8888/{service-name}/{profile}`
- `http://localhost:8888/{service-name}.yml`
- `http://localhost:8888/{service-name}.properties`

## Profiles

- `default` - Default configuration
- `dev` - Development environment
- `prod` - Production environment
- `test` - Test environment

## Environment Variables

The configuration uses environment variables for sensitive data:
- `DATABASE_HOST` - Database host
- `DATABASE_PORT` - Database port
- `DATABASE_USERNAME` - Database username
- `DATABASE_PASSWORD` - Database password
- `REDIS_HOST` - Redis host
- `REDIS_PORT` - Redis port
- `REDIS_PASSWORD` - Redis password
- `JWT_SECRET` - JWT secret key

## Usage

1. Start the Config Server
2. Services will automatically fetch their configuration
3. Configuration changes require service restart or refresh endpoint call
4. Use `/actuator/refresh` endpoint to reload configuration without restart

## File Location

All configuration files are now stored within the config-server project at:
`src/main/resources/config/`

This makes the configuration part of the repository and easier to manage.
