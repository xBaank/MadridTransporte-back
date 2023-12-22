FROM gradle:7.6.1-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test

FROM amazoncorretto:21.0.1
COPY . /build
RUN mkdir /app

COPY --from=build /home/gradle/src/busTrackerApi/build/libs/*.jar /app/busTrackerApi.jar
ENTRYPOINT ["java", "-jar", "/app/busTrackerApi.jar"]