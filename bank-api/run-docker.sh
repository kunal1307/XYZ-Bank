#!/bin/bash

set -e

echo "Building Maven package..."
mvn clean package

echo "Building Docker image..."
docker build -t bank-api .

echo "Running Docker container..."
docker run -p 8080:8080 \
  --add-host=host.docker.internal:host-gateway \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/yourdb \
  -e DB_USERNAME=your_DB_username \
  -e DB_PASSWORD=your_DB_password \
  bank-api