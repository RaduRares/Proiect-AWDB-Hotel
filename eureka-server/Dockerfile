FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY hotel-service/pom.xml hotel-service/pom.xml
COPY user-service/pom.xml user-service/pom.xml
COPY notif-service/pom.xml notif-service/pom.xml
RUN mvn -B dependency:go-offline -pl eureka-server --also-make -q
COPY eureka-server/src eureka-server/src
RUN mvn -B package -pl eureka-server -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/eureka-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
