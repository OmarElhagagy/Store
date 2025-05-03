# Clothing Store E-Commerce Platform

A full-featured e-commerce platform for clothing retail developed with Java Spring Boot and JPA.

## Table of Contents
- [Overview](#overview)
- [Database Structure](#database-structure)
- [Technology Stack](#technology-stack)
- [Features](#features)
- [Installation](#installation)
- [API Documentation](#api-documentation)
- [License](#license)

## Overview

This application is a complete e-commerce solution for clothing retailers, providing inventory management, customer relationship management, order processing, and an online storefront. It supports multi-store operations with robust stock management and employee role-based access control.

## Database Structure

The system utilizes PostgreSQL with the following data model:

### Core Entities

#### Store Management
- **Store**: Central entity for physical locations
- **Storage**: Inventory storage locations within stores
- **Employee**: Staff members with role-based permissions
- **Supplier**: Product vendors and their contact information

#### Product Management
- **Product**: Clothing items with size, color, and pricing details
- **Categories**: Product classification system
- **Store_Inventory**: Current stock levels by store
- **Reserved_Stock**: Items temporarily held for customers
- **Image**: Product images

#### Customer Management
- **Customer**: Core customer information
- **Users**: Authentication and security details
- **Sessions**: User session management
- **Addresses**: Customer shipping and billing addresses
- **Phone_Numbers_Customers**: Customer contact info

#### Order Processing
- **Customer_Order**: Order header information
- **Order_Details**: Line items in each order
- **Payment_Methods**: Available payment options
- **Return**: Product return tracking
- **Shipping**: Delivery tracking information

#### Marketing & Engagement
- **Promotions**: Discount codes and special offers
- **Cart**: Shopping cart functionality
- **Wishlist**: Saved items for future purchase
- **Reviews**: Customer product ratings and feedback
- **Notification**: Customer and employee alerts

### Entity Relationship Diagram

The database contains 30 tables with appropriate foreign key relationships and constraints to maintain data integrity.

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.x
- **ORM**: Hibernate with JPA Buddy
- **Database**: PostgreSQL
- **API**: RESTful services with JSON
- **Security**: Spring Security with JWT authentication
- **Documentation**: Swagger/OpenAPI

## Features

- Multi-store inventory management
- Role-based access control (Admin, Manager, Seller, Stockkeeper)
- Product catalog with categories, images, and detailed descriptions
- Customer account management
- Shopping cart and wishlist functionality
- Order processing and tracking
- Returns management
- Promotion and discount system
- Review and rating system
- Audit logging for security and compliance

## Installation

### Prerequisites
- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6+

### Database Setup
1. Create a PostgreSQL database
2. Run the included schema creation script:

```bash
psql -U postgres -d your_database_name -f schema.sql
```

### Application Configuration
1. Clone the repository:
```bash
git clone https://github.com/your-username/clothing-store.git
cd clothing-store
```

2. Configure database connection in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=validate
```

3. Build the application:
```bash
mvn clean install
```

4. Run the application:
```bash
java -jar target/clothing-store-0.0.1-SNAPSHOT.jar
```

The application will be available at http://localhost:8080

## API Documentation

API documentation is available via Swagger UI at http://localhost:8080/swagger-ui.html after starting the application.

## License

[MIT License](LICENSE)
