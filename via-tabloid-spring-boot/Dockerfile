# Stage 1: Build the application
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

# Copy maven executable and project definition
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Grant execution rights on the mvnw and fix line endings
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw


# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Copy the built artifact from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

