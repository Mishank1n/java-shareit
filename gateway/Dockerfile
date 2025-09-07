FROM amazoncorretto:21

VOLUME /tmp

COPY gateway/target/gateway-0.0.1-SNAPSHOT.jar /app.jar

ENV JAVA_OPTS=""

ENTRYPOINT sh -c "java $JAVA_OPTS -jar /app.jar"