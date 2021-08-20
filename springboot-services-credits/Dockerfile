FROM openjdk:18
VOLUME /tmp
ADD ./target/springboot-services-credits-0.0.1-SNAPSHOT.jar services-credits.jar
ENTRYPOINT ["java","-jar","/services-credits.jar"]