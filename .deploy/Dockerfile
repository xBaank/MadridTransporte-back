FROM gradle:7.6.1-jdk17 AS build
COPY --chown=gradle:gradle .. /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test

FROM openjdk:20-jdk-slim-buster
COPY .. /build
RUN mkdir /app

COPY --from=build /home/gradle/src/busTrackerApi/build/libs/*.jar /app/busTrackerApi.jar
ENTRYPOINT ["java", "-jar", "/app/busTrackerApi.jar"]