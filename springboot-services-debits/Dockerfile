FROM openjdk:18
VOLUME /tmp
ADD ./target/springboot-services-debits-0.0.1-SNAPSHOT.jar services-debits.jar
ENTRYPOINT ["java","-jar","/services-credits.jar"]