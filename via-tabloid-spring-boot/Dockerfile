# Stage 1: Build the application
FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /src
COPY via-tabloid-spring-boot/pom.xml .

# Copy the source code (two levels up to include contracts)
COPY via-tabloid-spring-boot/src src
COPY contracts ../contracts
# then set the working directory to src to build the application
# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Copy the built artifact from the build stage
COPY --from=build /src/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

