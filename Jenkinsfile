pipeline {
    agent any

    environment {
        DOCKER_HUB_USER = "brianchoi506"   // Docker Hub ID
        DOCKER_HUB_PASS = credentials('docker-hub-id') // Jenkins Credentials: Docker Hub 비밀번호 or 토큰
        EC2_HOST = "ubuntu@18.222.152.75"  // EC2 접속 계정+IP
        EC2_KEY = "~/.ssh/security-msa.pem"      // Jenkins 서버에 저장된 EC2 키 경로
    }

    stages {
        stage('Checkout') {
            steps {
                echo "=== Git Checkout ==="
                git branch: 'main', url: 'https://github.com/brian506/online-chat.git'
            }
        }

        stage('Build Jar') {
                            stage('Build Eureka Server'){
                                steps {
                                    dir('eureka-server'){
                                        echo 'Building Eureka Server ...'
                                        sh '../gradlew clean build -x test' // gradlew 파일이 루트디렉토리에 있으므로 ''../''
                                        // sh './gradlew :eureka-server:clean :eureka-server:build -x test' // 보통 멀티 모듈에서 이렇게 설정
                                    }
                                }
                            }
                            stage('Build Gateway Service') {
                                steps {
                                    dir('gateway-service') {
                                        echo 'Building Gateway Service ...'
                                        sh '../gradlew clean build -x test'
                                    }
                                }
                            }
                            stage('Build Auth Service') {
                                steps {
                                    dir('auth-service') {
                                        echo 'Building Auth Service ...'
                                        sh '../gradlew clean build -x test'
                                    }
                                }
                            }
                            stage('Build Chat Service') {
                                steps {
                                    dir('chat-service') {
                                        echo 'Building Chat Service ...'
                                        sh '../gradlew clean build -x test'

                                    }
                                }
                            }
                     }
                                                    


        stage('Build Docker Images') {
            steps {
                echo "=== Docker Build ==="
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
                echo "=== Docker Push to Hub ==="
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
                echo "=== Deploy on EC2 ==="
                // Secret file(.env.prod)을 Jenkins Credentials에서 가져와서 EC2로 복사
                withCredentials([file(credentialsId: 'env-file', variable: 'ENV_FILE')]) {
                    sh """
                      # EC2에 app 디렉토리 준비
                      ssh -i $EC2_KEY -o StrictHostKeyChecking=no $EC2_HOST 'mkdir -p ~/app'

                      # Secret file(.env.prod) 업로드
                      scp -i $EC2_KEY -o StrictHostKeyChecking=no $ENV_FILE $EC2_HOST:~/app/.env.prod

                      # EC2에서 배포 실행
                      ssh -i $EC2_KEY -o StrictHostKeyChecking=no $EC2_HOST '
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
        always {
            echo "=== Pipeline Finished ==="
        }
        success {
            echo "✅ 배포 성공!"
        }
        failure {
            echo "❌ 배포 실패!"
        }
    }
}
