FROM openjdk:18
VOLUME /tmp
ADD ./target/springboot-fixed-term-service-0.0.1-SNAPSHOT.jar fixed-term-service.jar
ENTRYPOINT ["java","-jar","/fixed-term-service.jar"]