FROM openjdk:11.0.7-jre-slim
COPY build/libs/demo-0.0.1-SNAPSHOT.jar service.jar
ENTRYPOINT [ "java","-jar","/service.jar" ]
EXPOSE 8080