# POC_Microservices

This repository contains a set of Java-based microservices as part of a Proof of Concept (POC) architecture.

## Microservices Included

- **auth-service**: JWT-based authentication and user management
- **fixed_apigateway**: Spring Cloud Gateway for routing and token validation
- **eureka-server**: Service discovery server
- **order-service**: Order management microservice
- **product-service**: Product catalog and related APIs

## Tech Stack

- Java 21
- Spring Boot 3.2.x
- Spring Cloud 2023.x
- Eureka for service discovery
- JWT for authentication
- Maven for build

## Run Instructions

Each service has its own `application.yml`. Use IntelliJ or VSCode to run services individually or via terminal:

```bash
cd auth-service
mvn spring-boot:run
![Architecture](https://github.com/rajeevmca11/POCMicroservices/blob/main/POC_HLD.png)
