FROM openjdk:18
VOLUME /tmp
ADD ./target/springboot-clients-service-0.0.1-SNAPSHOT.jar clients-service.jar
ENTRYPOINT ["java","-jar","/clients-service.jar"]