FROM openjdk:17-alpine
EXPOSE 8080
COPY target/easytakeaway-0.0.1-SNAPSHOT.jar easytakeaway.jar
ENTRYPOINT ["java","-jar","easytakeaway.jar"]