# Legacy Order Service

A legacy-style Java 11 SOAP web service with a standalone client application, to demonstrate enterprise Java fundamentals, service design, and API evolution.

This project is intentionally structured with a "before" state, for a future migration to modern Java 21 + Spring Boot.

## Project Structure

This repository is organized as a Maven multi-module project:

- `order-api`  
  Shared service contract and model classes
- `order-webservice`  
  SOAP web service implementation
- `order-client`  
  Standalone Java client for calling the SOAP service

## Features

### User Stories

- As a user, I want to **place an order** so that it is created in the system.
- As a user, I want to **view order details** so that I can review the current information and status.
- As a user, I want to **cancel my order** so that it is no longer active.
- As a user, I want to **change delivery details** so that I can update where the order should be sent.
- As a user, I want to **list my orders** so that I can see all orders associated with my account.
- As a system, I want to **update order status** so that orders can progress through the delivery lifecycle.

### Planned Order Lifecycle
- `CREATED`
- `PENDING`
- `SHIPPED`
- `DELIVERED`
- `CANCELLED`

## Technology Stack

- Java 11
- Maven
- SOAP / JAX-WS
- SLF4J + Log4j2
- IntelliJ IDEA
- Apache Tomcat

## Architecture

The project uses a classic SOAP-based setup:

- the **web service** exposes operations for creating and managing orders
- the **client** consumes the SOAP service without any UI
- the **API module** contains shared contracts and models

## Roadmap & Milestones

- [x] **Milestone 1:** [Legacy Java 11 SOAP Baseline]
- [ ] **Milestone 2:** Java 21 Upgrade (In Progress)
- [ ] **Milestone 3:** Spring Boot Integration
- [ ] **Milestone 4:** REST Endpoints & Modern API Design

## Running the Project

### Requirements

**Milestone 1:**

- Java 11
- Maven
- A servlet container such as Apache Tomcat

**Milestone 2+:**

- Java 21
- Maven 3.9.0 or newer

### Build
From the root of the project:

```bash
mvn clean install
```

### Deploy the Web Service
Deploy the generated WAR file from the webservice module to Tomcat.

### Run the Client
Run the client module from your IDE or from the command line after the service is running.

## Notes

This project is a work-in-progress. The initial version represents a legacy Java 11 SOAP service before modernization.

## License

Distributed under the MIT License. See LICENSE for more information.
