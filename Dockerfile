# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build

# Set the working directory
WORKDIR /app

# Copy the project files to the container
COPY . .

#not sure if i need this
RUN chmod +x ./gradlew && ./gradlew clean build -x test

# Run Gradle to build the application
RUN ./gradlew clean build -x test

# Runtime stage
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

