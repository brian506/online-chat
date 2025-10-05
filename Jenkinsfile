
pipeline {
  agent {
      dockerfile {
        filename 'Dockerfile.jenkins'
        // ✨ 이 부분이 핵심: 에이전트 컨테이너를 root 권한으로 실행
        args '-u root:root -v /var/run/docker.sock:/var/run/docker.sock'
      }
    }


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
          docker build -t $DOCKER_HUB_USER/gateway-server:1.0.3 ./gateway-server
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
          docker push $DOCKER_HUB_USER/gateway-server:1.0.3
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
          sh """
            ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no $EC2_HOST 'mkdir -p ~/app'
              scp -i "$SSH_KEY" -o StrictHostKeyChecking=no "$ENV_FILE" $EC2_HOST:~/app/.env.prod
              ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no $EC2_HOST '
                cd ~/app &&
                docker-compose pull &&
                docker-compose up -d
              '
          """
        }
      }
    }
  }

  post {
    always  { echo '=== Pipeline Finished ===' }
    cleanWs()
    success { echo '✅ 배포 성공!' }
    failure { echo '❌ 배포 실패!' }
  }
}
