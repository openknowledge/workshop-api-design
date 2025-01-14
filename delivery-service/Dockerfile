FROM maven:3.9.3-eclipse-temurin AS mvn

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
