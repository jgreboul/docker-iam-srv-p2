FROM openjdk:22-jdk-slim

ARG JAR_FILE=target/*.jar

COPY ./target/iam.service-0.0.1.jar /app/jgr_iam_service.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "jgr_iam_service.jar"]