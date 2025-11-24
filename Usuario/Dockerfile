FROM maven:3.9.7-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw -q package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=prod
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8003
ENTRYPOINT ["java","-jar","/app/app.jar"]
