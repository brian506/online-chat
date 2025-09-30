pipeline {
    agent any
    environment {
        COMPOSE_FILE = "docker-compose.yml"
    }

    // 파이프라인의 각 실행 단계
    stages {
        // 1. Git 저장소에서 최신 소스 코드 가져오기
        stage('Checkout') {
            steps {
                echo 'Checking out Git source code...'
                git branch: 'main', url: 'https://github.com/brian506/online-chat.git'
            }
        }
        // 2. 각 서비스 빌드
        // 도커 이미지 만들기 전에 애플리케이션 먼저 빌드(테스트는 여기서 안함 : -x)
        stage('Build Services'){
            parallel { // 병렬 수행으로 시간단축
                stage('Build Eureka Server') {
                    steps {
                        dir('eureka-server'){
                            echo 'Building Eureka Server ...'
                            sh './gradlew clean build -x test'
                        }
                    }
                }
                stage('Build Gateway Service') {
                    steps {
                        dir('gateway-service') {
                            echo 'Building Gateway Service ...'
                            sh './gradlew clean build -x test'
                        }
                    }
                }
                stage('Build Auth Service') {
                    steps {
                        dir('auth-service') {
                            echo 'Building Auth Service ...'
                            sh './gradlew clean build -x test'
                        }
                    }
                }
                stage('Build Chat Service') {
                    steps {
                        dir('chat-service') {
                            echo 'Building Chat Service ...'
                            sh './gradlew clean build -x test'
                        }
                    }
                }
            }
        }
        // 3. 비밀 정보(환경변수) 준비
        stage('Prepare .env') {
            steps {
                echo 'Loading .env file from Jenkins Credentials ...'
                withCredentials([file(credentialsId: 'my-env-file-credential',variable: 'ENV_FILE')]){
                    sh 'cp $ENV_FILE .env'
                }
            }
        }
        // 4. 이전 컨테이너 정리(무중단 배포시엔 X), 모든 서비스 중지,제거
        stage('Clean Previous Deployment') {
            steps {
                echo 'Stopping and removing existing containers ...'
                sh "docker-compose -f ${COMPOSE_FILE} down --remove-orphans || true"
            }
        }
        // 5. 도커 이미지 빌드 및 모든 서비스 시작
        stage('Deploy Services') {
            echo 'Building images and starting all services ...'
            sh "docker-compose -f ${COMPOSE_FILE} up --build -d"
        }
    }
}
// 6. 파이프라인 실행 후 항상 수행할 작업 정의
post {
    always {
        // env 파일 삭제
        echo 'Cleaning up secrets ...'
        sh 'rm -f .env'

        // workspace 정리
        cleanWs()
    }
    success {
        // 성공 시 알림
        echo 'Deployment was SUCCESSFUL!!'
    }
    failure {
        // 실패 시 알림
        echo 'Deployment FAILED!!'
    }
}