FROM amazoncorretto:11-alpine-jdk
EXPOSE 8080
ADD target/shareit-gateway.jar shareit-gateway.jar
ENTRYPOINT ["java","-jar","/shareit-gateway.jar"]
