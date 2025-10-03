# Jenkins LTS 기반
FROM jenkins/jenkins:lts

USER root

# docker CLI + docker-compose 설치
RUN apt-get update && \
    apt-get install -y docker.io docker-compose && \
    rm -rf /var/lib/apt/lists/*

# Jenkins 계정을 docker 그룹에 추가 (권한 문제 방지)
RUN usermod -aG docker jenkins

USER jenkins
