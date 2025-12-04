FROM eclipse-temurin:21-alpine AS maven-build
RUN apk add --no-cache dos2unix
COPY .mvn /build/.mvn
COPY mvnw pom.xml /build/
WORKDIR /build
RUN dos2unix mvnw
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
COPY src /build/src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-alpine AS application
COPY --from=maven-build /build/target/Quizlecik-projekt*.jar /opt/app.jar
WORKDIR /opt
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]