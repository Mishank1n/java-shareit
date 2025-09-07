FROM amazoncorretto:21

VOLUME /tmp

ARG JAR_FILE=gateway/target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]