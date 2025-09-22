echo "Building all modules with Gradle..."
./gradlew clean build -x test || {
  echo "Gradle build failed!"
  exit 1
}

# docker-compose.yml 파일명을 명시적으로 지정
DOCKER_COMPOSE_FILE="docker-compose-local.yml"

echo "Building Docker images..."
docker compose -f "$DOCKER_COMPOSE_FILE" build --no-cache || {
  echo "Docker build failed!"
  exit 1
}

echo "Starting containers..."
docker compose -f "$DOCKER_COMPOSE_FILE" up -d || {
  echo "Docker compose up failed!"
  exit 1
}

echo "All done! Containers are up and running."
