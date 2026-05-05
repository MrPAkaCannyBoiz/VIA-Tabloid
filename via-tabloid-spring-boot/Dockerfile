FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /src
COPY via-tabloid-spring-boot/pom.xml .
COPY via-tabloid-spring-boot/src src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /src/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
