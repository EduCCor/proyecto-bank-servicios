FROM openjdk:18
VOLUME /tmp
EXPOSE 8888
ADD ./target/springboot-config-server-0.0.1-SNAPSHOT.jar config-server.jar
ENTRYPOINT ["java","-jar","/config-server.jar"]