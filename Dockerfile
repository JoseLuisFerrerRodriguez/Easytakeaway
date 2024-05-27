FROM openjdk:17-alpine
EXPOSE 8080
WORKDIR /app
COPY target/easytakeaway-0.0.1-SNAPSHOT.jar easytakeaway.jar
COPY content /app/content
ENTRYPOINT ["java","-jar","easytakeaway.jar"]