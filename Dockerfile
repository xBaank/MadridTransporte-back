FROM openjdk:17-jdk-slim-buster
COPY . /build
WORKDIR /build
RUN chmod +xr ./gradlew
RUN ./gradlew build
COPY  busTrackerApi/build/libs/busTrackerApi.jar /app/busTrackerApi.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "busTrackerApi.jar"]