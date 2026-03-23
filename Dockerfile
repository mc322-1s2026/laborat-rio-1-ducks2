FROM eclipse-temurin:21-jdk-jammy
RUN apt-get update && apt-get install -y maven git
WORKDIR /workspace