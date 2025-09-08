#!/bin/bash

AUTH_MODULE_DIR="./auth-service"
EUREKA_MODULE_DIR="./eureka-server"
GATEWAY_MODULE_DIR="./gateway-service"

echo "Building the jar with Gradle..."
./gradlew -p "$EUREKA_MODULE_DIR" clean build -x test || {
  echo "Gradle build failed!"
  exit 1
}

echo "Building the jar with Gradle..."
./gradlew -p "$GATEWAY_MODULE_DIR" clean build -x test || {
  echo "Gradle build failed!"
  exit 1
}

echo "Building the jar with Gradle..."
./gradlew -p "$AUTH_MODULE_DIR" clean build -x test || {
  echo "Gradle build failed!"
  exit 1
}


echo "Building Docker images..."
docker compose -f docker-compose-local.yml build --no-cache || {
  echo "Docker build failed!"
  exit 1
}

echo "Starting containers..."
docker compose -f docker-compose-local.yml up -d || {
  echo "Docker compose up failed!"
  exit 1
}

echo "All done! Containers are up and running."