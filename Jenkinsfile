
pipeline {
  agent any

  environment {
    DOCKER_HUB_USER = 'brianchoi506'
    DOCKER_HUB_PASS = credentials('docker-hub-id')   // Secret text
    EC2_HOST = 'ubuntu@18.222.152.75'
  }

  stages {
    stage('Build Jar') {
      steps {
        echo '=== Gradle Build ==='
        sh 'chmod +x ./gradlew || true'
        sh './gradlew :eureka-server:clean :eureka-server:build -x test'
        sh './gradlew :gateway-service:clean :gateway-service:build -x test'
        sh './gradlew :auth-service:clean :auth-service:build -x test'
        sh './gradlew :chat-service:clean :chat-service:build -x test'
      }
    }

    stage('Build Docker Images') {
      steps {
        echo '=== Docker Build ==='
        sh """
          docker build -t $DOCKER_HUB_USER/eureka-server:1.0.3 ./eureka-server
          docker build -t $DOCKER_HUB_USER/gateway-service:1.0.3 ./gateway-service
          docker build -t $DOCKER_HUB_USER/auth-service:1.0.3 ./auth-service
          docker build -t $DOCKER_HUB_USER/chat-service:1.0.3 ./chat-service
        """
      }
    }

    stage('Push Docker Images') {
      steps {
        echo '=== Docker Push to Hub ==='
        sh """
          echo $DOCKER_HUB_PASS | docker login -u $DOCKER_HUB_USER --password-stdin
          docker push $DOCKER_HUB_USER/eureka-server:1.0.3
          docker push $DOCKER_HUB_USER/gateway-service:1.0.3
          docker push $DOCKER_HUB_USER/auth-service:1.0.3
          docker push $DOCKER_HUB_USER/chat-service:1.0.3
        """
      }
    }

stage('Deploy to EC2') {
  steps {
    echo '=== Deploy on EC2 (scp + ssh) ==='
    withCredentials([
      file(credentialsId: 'env-file',          variable: 'ENV_FILE'),
      sshUserPrivateKey(credentialsId: 'ec2-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')
    ]) {

      sh '''
        set -euo pipefail
        HOST=${EC2_HOST#*@}
        USER=$SSH_USER

        echo "[1/4] connect test"
        ssh -i "$SSH_KEY" -o IdentitiesOnly=yes -o StrictHostKeyChecking=no "$USER@$HOST" 'id && whoami'

        echo "[2/4] prepare dir on remote"
        # 반드시 원격 블록을 '따옴표'로 묶어서 전체가 원격에서 실행되게 하기
        ssh -i "$SSH_KEY" -o IdentitiesOnly=yes -o StrictHostKeyChecking=no "$USER@$HOST" '
          set -e
          mkdir -p /home/ubuntu/app
          ls -ld /home/ubuntu/app
        '

        echo "[3/4] upload files via scp (no sudo)"
        # 절대경로로 지정 (~/ 대신 /home/ubuntu 사용)
        scp -i "$SSH_KEY" -o IdentitiesOnly=yes -o StrictHostKeyChecking=no \
          "$ENV_FILE" "$USER@$HOST":/home/ubuntu/app/.env.prod
        scp -i "$SSH_KEY" -o IdentitiesOnly=yes -o StrictHostKeyChecking=no \
          docker-compose.yml "$USER@$HOST":/home/ubuntu/app/docker-compose.yml

        echo "[4/4] deploy with docker compose"
        ssh -i "$SSH_KEY" -o IdentitiesOnly=yes -o StrictHostKeyChecking=no "$USER@$HOST" '
          set -e
          cd /home/ubuntu/app
          (docker compose pull || docker-compose pull)
          (docker compose up -d --remove-orphans || docker-compose up -d --remove-orphans)
          docker image prune -af || true
        '
      '''
        }
      }
    }
  }

  post {
    always  {
    echo '=== Pipeline Finished ==='
    cleanWs()
    }

    success { echo '✅ 배포 성공!' }
    failure { echo '❌ 배포 실패!' }
  }
}