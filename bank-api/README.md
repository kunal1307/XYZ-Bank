# Bank API

This project provides REST APIs for customer registration, login, and account overview as part of a backend service.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven
- JUnit & MockMvc for testing
- Swagger UI for API documentation

---

## Features

### 1. Customer Registration
- Register a new customer with name, address, username, date of birth, and country
- Username must be unique
- Only customers from NL and BE are allowed
- Customer must be at least 18 years old
- Generates:
    - Default password
    - IBAN account number

### 2. Login
- Login using username and generated password

### 3. Account Overview
- Retrieve account details:
    - IBAN
    - Account type
    - Balance
    - Currency

---

## API Documentation

Swagger UI available at:

[URL To open swagger api in ui edition](http://localhost:8080/swagger-ui/index.html)

OpenAPI specification is available in:

[openapi.yaml](src/main/resources/openapi.yaml)

you can also check in the browser after starting your appilcation
Open the [open api doc URL](http://localhost:8080/v3/api-docs) 


---

## How to Run


### Running without docker 


### Prerequisites

1. PostgreSQL running locally 
2. Update `application.yaml` with 


Run:

`mvn clean install`

`DB_URL=<Database URL> DB_USERNAME=postgres DB_PASSWORD='Kunal@123' mvn clean spring-boot:run`

For this project a sample URL for Postgresql : jdbc:postgresql://localhost:5432/<db_name>

###Running with docker

You can run the application using Docker as an alternative to running it locally.

### Prerequisites

1. Docker installed and running
2. PostgreSQL running locally

---

### Run using script

#### Mac/Linux

1. Make the script executable (only once) this is to give it permission:

`chmod +x run-docker.sh`


2. Run the script:

`./run-docker.sh`


---

#### Windows

Run:

`run-docker.bat`


---

### What the script does

- Builds the application using Maven
- Builds the Docker image
- Runs the container with required environment variables

---

### Environment Variables

The application expects the following environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

Example:

jdbc:postgresql://host.docker.internal:5432/postgres


---

### Access the application

After starting:

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html

---

### Notes

- `host.docker.internal` is used to allow the Docker container to access the local PostgreSQL instance.
- On Linux, additional configuration may be required for this to work.



---

## Testing

Run tests:

`mvn test`


Includes:
- Service layer tests (business logic)
- Controller tests (API behavior)

---

## Postman collection


After running, you application using anyone option either bash or docker you verify by accessing the end points and hitting those through post man which can be imported in postman,
Collection you will find it [here](Bank%20API%20-%20Assignment.postman_collection.json)

---

## Design Decisions

### IBAN Generation
IBAN is generated using:
- Country code (NL)
- Random 2-digit check value (format only)
- Bank code (RBAN)
- Account number based on timestamp + randomness

Note: Full IBAN checksum validation (mod-97) is not implemented to keep the solution simple.

---

### Rate Limiting
A global rate limiter is implemented to handle the constraint of limited database capacity.

- Allows ~2 requests per second
- Swagger endpoints are excluded to allow API documentation to function properly

---

## Possible Improvements

- Implement proper IBAN checksum validation (mod-97)
- Add caching (e.g., for `/overview`) to reduce database load
- Introduce authentication tokens instead of plain login
- Improve rate limiting (per user/IP instead of global)
- Add Docker support for easier deployment

---

## Notes

The implementation focuses on clarity and correctness while keeping the solution simple and easy to understand.