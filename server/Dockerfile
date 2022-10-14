FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar server_app.jar
ENTRYPOINT ["java", "-jar", "/server_app.jar"]