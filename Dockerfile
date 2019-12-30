FROM openjdk:11.0.4-jre-slim

RUN mkdir /app

WORKDIR /app

ADD ./api/target/api-1.1-SNAPSHOT.jar /app

EXPOSE 8081 9001

CMD ["java", "-jar", "api-1.1-SNAPSHOT.jar"]
