FROM openjdk:18
VOLUME /tmp
ADD ./target/springboot-create-account-service-0.0.1-SNAPSHOT.jar create-account-service.jar
ENTRYPOINT ["java","-jar","/create-account-service.jar"]