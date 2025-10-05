# Jenkins Agent의 기반이 되는 이미지를 사용합니다.
FROM jenkins/agent:latest-jdk17

# root 권한으로 전환하여 필요한 도구들을 설치합니다.
USER root

# Docker CLI, Git 등 필수 도구 설치
RUN apt-get update && \
    apt-get install -y git ca-certificates curl gnupg lsb-release && \
    mkdir -p /etc/apt/keyrings && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg && \
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/debian $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null && \
    apt-get update && \
    apt-get install -y docker-ce-cli && \
    apt-get clean

# 다시 기본 jenkins 유저로 전환합니다.
USER jenkins