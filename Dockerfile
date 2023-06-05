FROM openjdk:17-jdk-slim
WORKDIR /spring
ARG JAR_FILE=/build/libs/eatery-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /spring/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-Duser.timezone=Asia/Seoul", "-jar", "/spring/app.jar"]
#ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Duser.timezone=Asia/Seoul", "-jar", "/spring/app.jar"]
