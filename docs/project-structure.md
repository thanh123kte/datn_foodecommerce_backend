# QtiFOOD - Project Structure Documentation

## Project Overview

**QtiFOOD** is a backend application for a food ordering system built with Spring Boot 3.5.6 and Java 21. The project uses PostgreSQL as the database and applies Clean Architecture with clearly separated layers.

## Basic Information

- **Project Name**: QtiFOOD
- **Version**: 0.0.1-SNAPSHOT
- **Java Version**: 21
- **Spring Boot**: 3.5.6
- **Database**: PostgreSQL
- **Build Tool**: Gradle
- **Package**: com.example.qtifood

## Main Directory Structure

```
qtifood/
├── build.gradle                 # Gradle configuration file
├── gradlew                      # Gradle wrapper (Unix)
├── gradlew.bat                  # Gradle wrapper (Windows)
├── settings.gradle              # Gradle settings configuration
├── HELP.md                      # Basic guide
├── docs/                        # Project documentation
├── gradle/wrapper/              # Gradle wrapper files
├── build/                       # Build output directory
└── src/                         # Source code
    ├── main/
    │   ├── java/com/example/qtifood/
    │   └── resources/
    └── test/
```

## Detailed Source Code Structure

### `src/main/java/com/example/qtifood/`

```
qtifood/
├── QtifoodApplication.java      # Main application class
├── config/                      (Spring configuration classes)
├── controllers/                 # REST Controllers
│   └── UserController.java     # User management endpoints
├── dtos/                        # Data Transfer Objects
│   └── user/
│       ├── CreateUserRequestDto.java
│       ├── UpdateUserRequestDto.java
│       └── UserResponseDto.java
├── entities/                    # JPA Entities
│   └── User.java               # User entity
├── enums/                      (Constants, order status, user roles, etc.)
├── exceptions/                 # Custom exceptions (not yet implemented)
├── mappers/                    (DTO <-> Entity mapping, using MapStruct or manual)
├── repositories/               # Data Access Layer
│   └── UserJpaRepository.java  # User repository
├── security/                   (JWT login classes like JwtFilter, AuthEntryPoint)
├── services/                   # Business Logic Layer
│   ├── UserService.java        # User service interface
│   └── impl/                   (Implementation classes go here)
│       └── UserServiceImpl.java # User service implementation
└── utils/                      (Helper functions, converters, email sender, etc.)
```

### `src/main/resources/`

```
resources/
├── application.properties       # Application configuration
├── static/                     # Static web resources
└── templates/                  # Template files (Thymeleaf)
```

## Directory Functions and Purposes

### 📁 **controllers/**

- **Purpose**: Contains REST Controllers that handle HTTP requests
- **Role**: Presentation Layer - receives and responds to API requests
- **Current**: `UserController.java` - manages CRUD operations for User
- **Pattern**: RESTful API endpoints with mappings like `/api/users`

### 📁 **dtos/**

- **Purpose**: Data Transfer Objects - classes to transfer data between layers
- **Role**: Data encapsulation, validation, and mapping between client-server
- **Structure**: Organized by domain (user, product, order...)
- **Examples**:
  - `CreateUserRequestDto`: Data for creating new user
  - `UpdateUserRequestDto`: Data for updating user
  - `UserResponseDto`: Data returned to client

### 📁 **entities/**

- **Purpose**: JPA Entity classes representing database tables
- **Role**: Data Layer - mapping with database schema
- **Annotations**: Uses JPA annotations (@Entity, @Table, @Column...)
- **Current**: `User.java` - entity for users table

### 📁 **repositories/**

- **Purpose**: Data Access Layer - interfaces for database access
- **Role**: Abstraction layer for database operations
- **Pattern**: Spring Data JPA repositories
- **Current**: `UserJpaRepository.java` - CRUD operations for User

### 📁 **services/**

- **Purpose**: Business Logic Layer
- **Role**: Handles business logic, orchestrates operations
- **Pattern**: Service Interface + Implementation
- **Structure**:
  - Interface: `UserService.java`
  - Implementation: `impl/UserServiceImpl.java`

### 📁 **config/** (Already exists in structure)

- **Purpose**: Spring configuration classes
- **Planned**: Database config, Security config, Bean definitions
- **Examples**: `DatabaseConfig.java`, `SecurityConfig.java`

### 📁 **enums/**  (Coming soon)

- **Purpose**: Enum constants for the system
- **Role**: Define constants, statuses, user roles
- **Planned**:
  - `UserRole.java` - User roles (ADMIN, CUSTOMER, SELLER)
  - `OrderStatus.java` - Order statuses (PENDING, CONFIRMED, DELIVERED)
  - `PaymentMethod.java` - Payment methods (CASH, CARD, ONLINE)

### 📁 **exceptions/** (Not yet implemented)

- **Purpose**: Custom exception classes and exception handlers
- **Role**: Centralized error handling and meaningful error messages
- **Planned**:
  - `UserNotFoundException.java` - User not found error
  - `GlobalExceptionHandler.java` - Global error handling
  - `ValidationException.java` - Data validation errors

### 📁 **mappers/**  (To be added)

- **Purpose**: Mapping between DTO and Entity objects
- **Role**: Clean data conversion between layers
- **Implementation**: Can use MapStruct or manual mapping
- **Planned**:
  - `UserMapper.java` - Map User DTO <-> Entity
  - `ProductMapper.java` - Map Product DTO <-> Entity
  - `OrderMapper.java` - Map Order DTO <-> Entity

### 📁 **security/**  (For JWT and Authentication)

- **Purpose**: Security configuration and authentication/authorization
- **Role**: Secure API endpoints and manage authentication
- **Planned**:
  - `JwtFilter.java` - Filter for handling JWT tokens
  - `AuthEntryPoint.java` - Entry point for authentication
  - `SecurityConfig.java` - Spring Security configuration
  - `JwtUtils.java` - Utilities for JWT operations

### 📁 **utils/**  (Helper functions and utilities)

- **Purpose**: Utility classes and helper methods
- **Role**: Common functions used throughout the application
- **Planned**:
  - `PasswordEncoder.java` - Encode/decode passwords
  - `DateUtils.java` - Date formatting and calculations
  - `ValidationUtils.java` - Custom validation methods
  - `EmailSender.java` - Email sending utilities
  - `FileUploadUtils.java` - File upload/download helpers

## Main Dependencies

### Core Dependencies

- **Spring Boot Starter Web**: REST API development
- **Spring Boot Starter Data JPA**: Database operations
- **Spring Boot Starter Validation**: Input validation
- **PostgreSQL Driver**: Database connectivity
- **Lombok**: Code generation (getters, setters, builders)

### Development Dependencies

- **Spring Boot DevTools**: Hot reload during development
- **Spring Boot Starter Test**: Unit and integration testing

## Database Configuration

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/qtifood_db
spring.datasource.username=postgres
spring.datasource.password=123456
spring.jpa.hibernate.ddl-auto=update
```

## Current API Endpoints

### User Management

- `GET /api/users` - Get list of all users
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

## Build and Run Application

```bash
# Build project
./gradlew build

# Run application
./gradlew bootRun

# Run tests
./gradlew test
```

## Reference Documentation

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA Reference](https://spring.io/projects/spring-data-jpa)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
