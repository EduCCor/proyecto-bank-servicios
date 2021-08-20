FROM openjdk:18
VOLUME /tmp
ADD ./target/springboot-services-saving-0.0.1-SNAPSHOT.jar services-saving.jar
ENTRYPOINT ["java","-jar","/services-saving.jar"]