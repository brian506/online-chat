#!/bin/bash

# docker-compose.yml 파일명을 변수로 지정
DOCKER_COMPOSE_FILE="docker-compose-local.yml"

# 1. 인자(서비스 이름)가 있는지 확인
SERVICE_NAME=$1
if [ -z "$SERVICE_NAME" ]; then
  echo "❌ Error: 재시작할 서비스 이름(모듈명)이 입력되지 않았습니다."
  echo "Usage: ./go.sh <service-name>"
  echo "Example: ./go.sh auth-service"
  exit 1
fi

echo "=================================================="
echo "🚀 Starting redeployment for: $SERVICE_NAME"
echo "=================================================="

# 2. Gradle로 해당 모듈만 콕 집어서 빌드 (전체 빌드 X)
echo "🛠️  Gradle Building (:$SERVICE_NAME)..."
./gradlew :$SERVICE_NAME:clean :$SERVICE_NAME:build -x test || {
  echo "❌ Gradle build failed!"
  exit 1
}

# 3. Docker Compose로 해당 서비스만 강제 재빌드 및 재시작
#    -d: 백그라운드 실행
#    --build: 해당 서비스의 이미지를 강제로 다시 빌드 (새로운 JAR 반영)
#    --force-recreate: 기존 컨테이너를 강제로 삭제하고 새로 생성
#    $SERVICE_NAME: 이 모든 작업을 이 서비스에만 한정 (다른 컨테이너는 건드리지 않음)
echo "🐳 Docker Re-creating ($SERVICE_NAME)..."
docker compose -f "$DOCKER_COMPOSE_FILE" up -d --build --force-recreate $SERVICE_NAME || {
  echo "❌ Docker compose up failed!"
  exit 1
}

echo ""
echo "✅ Success! $SERVICE_NAME is up and running."