FROM openjdk:18
VOLUME /tmp
ADD ./target/springboot-movements-service-0.0.1-SNAPSHOT.jar movements-service.jar
ENTRYPOINT ["java","-jar","/movements-service.jar"]