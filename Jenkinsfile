
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
         sh '''
                set -e
                HOST=${EC2_HOST#*@}
                USER=$SSH_USER

                # 0) 접속 확인
                ssh -i "$SSH_KEY" -o IdentitiesOnly=yes -o StrictHostKeyChecking=no $USER@$HOST 'id && whoami'

                # 1) (원격에서 한 번에) 디렉토리 생성 + 소유/권한 정리  ⬅️ 반드시 따옴표로 감싸기!
                ssh -i "$SSH_KEY" -o IdentitiesOnly=yes -o StrictHostKeyChecking=no $USER@$HOST '
                  set -e
                  sudo install -d -o ubuntu -g ubuntu -m 775 /home/ubuntu/app
                '
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
         sh """
                     set -e # 명령어 실행 중 오류가 발생하면 즉시 중단합니다.

                     # environment 블록에 정의된 EC2_HOST 변수에서 호스트 주소만 추출합니다.
                     HOST=\$(echo "$EC2_HOST" | cut -d'@' -f2)

                     # --- 1단계: 원격 서버에 폴더 생성 ---
                     # 'ubuntu' 유저의 권한으로 자신의 홈 디렉토리(~)에 폴더를 생성합니다.
                     # 'sudo'를 사용하지 않는 것이 권한 문제를 피하는 핵심입니다.
                     echo "--> Creating directory on EC2..."
                     ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no "$SSH_USER@\$HOST" 'mkdir -p ~/app'

                     # --- 2단계: scp로 파일 직접 복사 ---
                     # 가장 단순하고 확실한 방법으로 필요한 파일들을 EC2에 전송합니다.
                     echo "--> Copying files to EC2..."
                     scp -i "$SSH_KEY" -o StrictHostKeyChecking=no "$ENV_FILE" "$SSH_USER@\$HOST":~/app/.env.prod
                     scp -i "$SSH_KEY" -o StrictHostKeyChecking=no docker-compose.yml "$SSH_USER@\$HOST":~/app/docker-compose.yml

                     # --- 3단계: 원격 서버에서 Docker Compose 실행 ---
                     # EC2 서버로 접속하여 컨테이너를 최신 버전으로 실행합니다.
                     echo "--> Deploying with Docker Compose on EC2..."
                     ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no "$SSH_USER@\$HOST" '
                       cd ~/app
                       # docker compose (v2) 또는 docker-compose (v1) 명령어 모두 호환되도록 실행합니다.
                       (docker compose pull || docker-compose pull) &&
                       (docker compose up -d --remove-orphans || docker-compose up -d --remove-orphans) &&
                       echo "--> Cleaning up old images on EC2..."
                       docker image prune -af
                     '
                   """

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

                # 2) 업로드 (리다이렉션 대신 tee, 절대경로) — 필요시 sudo tee 사용
                ssh -i "$SSH_KEY" -o IdentitiesOnly=yes -o StrictHostKeyChecking=no $USER@$HOST \
                  "tee /home/ubuntu/app/.env.prod >/dev/null" < "$ENV_FILE"

                ssh -i "$SSH_KEY" -o IdentitiesOnly=yes -o StrictHostKeyChecking=no $USER@$HOST \
                  "tee /home/ubuntu/app/docker-compose.yml >/dev/null" < docker-compose.yml

                # 3) 배포
                ssh -i "$SSH_KEY" -o IdentitiesOnly=yes -o StrictHostKeyChecking=no $USER@$HOST '
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