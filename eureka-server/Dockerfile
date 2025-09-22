FROM --platform=linux/arm64 openjdk:21-jdk-slim

USER root
RUN apt-get update && apt-get install -y curl # curl 명령어 사용하기 위해

WORKDIR /app

ARG JAR_FILE=build/libs/eureka-server-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]