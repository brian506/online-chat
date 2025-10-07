
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
        echo '=== Deploy on EC2 ==='
        // Secret file(.env.prod) + SSH Key(EC2) 두 개 크리덴셜 사용
        withCredentials([
          file(credentialsId: 'env-file',          variable: 'ENV_FILE'),
          sshUserPrivateKey(credentialsId: 'ec2-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')
        ]) {
                            // ✨ sudo를 제거하여 모든 문제를 해결합니다.
                            sh '''
                              # 1. ubuntu 유저 권한으로 홈 디렉토리에 폴더 생성 (sudo 불필요)
                              ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no $SSH_USER@${EC2_HOST#*@} 'mkdir -p /home/ubuntu/app'

                              # 2. 필요한 파일들 전송 (docker-compose.yml도 함께 전송)
                              scp -i "$SSH_KEY" -o StrictHostKeyChecking=no "$ENV_FILE" $SSH_USER@${EC2_HOST#*@}:/home/ubuntu/app/.env.prod
                              scp -i "$SSH_KEY" -o StrictHostKeyChecking=no docker-compose.yml $SSH_USER@${EC2_HOST#*@}:/home/ubuntu/app/docker-compose.yml

                              # 3. EC2에서 docker compose 실행
                              ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no $SSH_USER@${EC2_HOST#*@} '
                                set -e
                                cd /home/ubuntu/app
                                (docker compose pull || docker-compose pull) &&
                                (docker compose up -d --remove-orphans || docker-compose up -d --remove-orphans)
                                echo "Cleaning up old images..."
                                docker image prune -af
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
