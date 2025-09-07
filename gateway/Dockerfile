FROM amazoncorretto:21
VOLUME /tmp
COPY gateway/target/gateway-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]