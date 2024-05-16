FROM alpine/git:2.45.2 AS git

RUN git clone https://github.com/apache/geronimo-jwt-auth.git

FROM maven:3.9.3-eclipse-temurin AS geronimo

RUN mkdir -p /usr
COPY --from=git /git/geronimo-jwt-auth /usr/geronimo-jwt-auth
WORKDIR /usr/geronimo-jwt-auth
RUN mvn clean install -DskipTests

FROM maven:3.9.3-eclipse-temurin AS mvn

RUN mkdir -p /root/.m2
COPY --from=geronimo /root/.m2 /root/.m2/
RUN mkdir -p /usr
WORKDIR /usr/src/online-shop
COPY pom.xml ./
RUN mvn package dependency:go-offline # cache dependencies
COPY src ./src
RUN mvn clean package -Dservice.name=delivery-service -DskipTests

FROM eclipse-temurin:17.0.12_7-jdk-ubi9-minimal

EXPOSE 8080
COPY --from=mvn /usr/src/online-shop/target/runner/meecrowave-core-runner.jar /opt/meecrowave-runner.jar
COPY --from=mvn /usr/src/online-shop/target/delivery-service.war /opt/delivery-service.war

ENTRYPOINT ["java", "--illegal-access=permit", "-Djava.net.preferIPv4Stack=true", "-jar", "/opt/meecrowave-runner.jar", "--webapp", "/opt/delivery-service.war"]
