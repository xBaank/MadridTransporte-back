FROM openjdk:17-jdk-slim-buster
COPY  /busTrackerApi/build/libs/busTrackerApi.jar /app/busTrackerApi.jar
ENTRYPOINT ["java", "-jar", "/app/busTrackerApi.jar"]