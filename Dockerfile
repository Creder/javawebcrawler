FROM openjdk:14-jdk-alpine
VOLUME /temp

ADD target/javawebcrawler-0.0.1-SNAPSHOT.jar javawebcrawler-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/javawebcrawler-0.0.1-SNAPSHOT.jar"]