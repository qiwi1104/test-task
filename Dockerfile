FROM amazoncorretto:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} test-task.jar
ENTRYPOINT ["java", "-jar", "/test-task.jar"]