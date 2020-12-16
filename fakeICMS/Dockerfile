FROM maven:3.6.3-openjdk-14
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn install

FROM openjdk:14-alpine
WORKDIR /usr/src/fakeicms
COPY --from=0 /app/target/fakeICMS-0.0.1-SNAPSHOT-spring-boot.jar /usr/src/fakeicms
ENTRYPOINT java -jar /usr/src/fakeicms/fakeICMS-0.0.1-SNAPSHOT-spring-boot.jar
