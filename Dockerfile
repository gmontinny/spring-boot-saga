# Stage 1: Build
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src/ src/
COPY data/ data/

RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Runtime
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar
COPY data/ data/

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
