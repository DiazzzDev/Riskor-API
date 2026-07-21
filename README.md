# Riskor API

Riskor API is the backend service powering the Riskor Occupational Health and Safety platform.

Built with Spring Boot and Java 17, the API provides secure RESTful endpoints for workplace incident management, occupational safety documentation, Personal Protective Equipment (PPE) administration, user authentication, reporting, and file management.

The project was developed as an MVP focused on learning enterprise backend development while implementing security and infrastructure components from scratch instead of relying on third-party identity providers.

## Overview

Riskor API centralizes the business logic required to manage occupational health and safety processes within an organization.

Its responsibilities include:

- Workplace incident management
- Investigation workflows
- User authentication and authorization
- PPE inventory management
- Occupational safety regulations
- File storage
- Email notifications
- Report generation

The API communicates with the Riskor Web Frontend through RESTful endpoints and follows a layered architecture to separate controllers, services, repositories, and persistence models.

## Key Features

### Workplace Incident Management

Provides endpoints for reporting, updating, and managing workplace incidents while maintaining historical records required during internal investigations.

### Investigation Support

Allows administrators to document investigation results, contributing factors, corrective actions, and preventive measures associated with workplace incidents.

### Authentication & Authorization

Authentication is implemented using Spring Security with a custom JWT-based authentication and authorization flow.

The project intentionally avoids external identity providers such as Auth0 or Clerk to provide hands-on experience with Spring Security and token-based authentication.

Security implementation includes:

- JWT Access Tokens
- HS256 symmetric signing
- Stateless authentication
- Route protection
- Role-based authorization
- Password encryption

### Password Security

User passwords are hashed using Argon2id through Spring Security's Argon2PasswordEncoder.

Argon2id is a modern password hashing algorithm designed to provide strong protection against brute-force attacks while resisting GPU and ASIC-based cracking techniques.

### File Storage

Riskor integrates Cloudinary as its cloud storage provider.

Uploaded files such as incident evidence and supporting documentation are securely stored outside the application server, reducing storage requirements while improving scalability.

### Email Notifications

Spring Mail is used to automatically notify administrators whenever a new workplace incident is reported.

These notifications help organizations react quickly to potential occupational safety events.

### Reporting

The API supports report generation using JasperReports and Apache POI, enabling organizations to export operational information for documentation and internal analysis.

## Architecture

The application follows a traditional layered architecture composed of:

```
Controller Layer
        │
        ▼
Service Layer
        │
        ▼
Repository Layer
        │
        ▼
Oracle Database
```

This separation improves maintainability, scalability, and testability by isolating business logic from persistence and presentation concerns.

## Technology Stack

### Backend

- Java 17
- Spring Boot 3
- Spring MVC
- Spring Data JPA
- Spring Security
- Hibernate
- Maven

### Database

- Oracle Cloud Database
- Oracle JDBC Driver

### Security

- Spring Security
- JWT (JJWT)
- HS256
- Argon2id Password Hashing
- Bouncy Castle

### Cloud Services

- Oracle Cloud Infrastructure
- Cloudinary

### Additional Technologies

- Thymeleaf
- Jakarta Validation
- Java Mail Sender
- JasperReports
- Apache POI
- Lombok

## Security Highlights

- Stateless authentication
- JWT token validation
- Symmetric HS256 token signing
- Argon2id password hashing
- Role-based authorization
- Request validation
- Layered architecture

## Frontend Repository

Riskor Web Frontend:

https://github.com/DiazzzDev/Riskor

## Project Goals

This project was built to:

- Learn enterprise backend development with Spring Boot
- Explore Spring Security internals
- Implement JWT authentication without third-party providers
- Practice secure password storage using Argon2id
- Integrate cloud services into a REST API
- Apply layered architecture principles
- Build a complete MVP for Occupational Health and Safety management

## Disclaimer

The authentication system was intentionally implemented from scratch as part of the project's educational objectives.

For production environments with large-scale deployments, managed identity providers such as Keycloak, Auth0, Clerk, AWS Cognito, or Microsoft Entra ID are generally recommended depending on the organization's security and operational requirements.

## License

This project is intended for educational and portfolio purposes unless otherwise specified.
