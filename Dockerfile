FROM openjdk:11.0.7-jre-slim
COPY src/main/generated/autotests-cloud-backend-0.0.1.jar service.jar
ENTRYPOINT [ "java","-jar","/service.jar" ]
EXPOSE 8080