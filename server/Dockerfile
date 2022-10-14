FROM amazoncorretto:11-alpine-jdk
EXPOSE 9090
ADD target/shareit-0.0.1-SNAPSHOT.jar shareit-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/shareit-0.0.1-SNAPSHOT.jar"]