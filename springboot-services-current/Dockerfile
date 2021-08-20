FROM openjdk:18
VOLUME /tmp
ADD ./target/springboot-services-current-0.0.1-SNAPSHOT.jar services-current.jar
ENTRYPOINT ["java","-jar","/services-current.jar"]