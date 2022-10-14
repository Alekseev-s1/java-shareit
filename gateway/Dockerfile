FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar gateway_app.jar
ENTRYPOINT ["java", "-jar", "/gateway_app.jar"]