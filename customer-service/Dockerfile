FROM maven:3.9.3-eclipse-temurin AS mvn

RUN mkdir -p /usr
WORKDIR /usr/src/online-shop
COPY pom.xml ./
RUN mvn package dependency:go-offline # cache dependencies
COPY src ./src
RUN mvn clean package -Dservice.name=customer-service -DskipTests

FROM eclipse-temurin:17.0.12_7-jdk-ubi9-minimal

EXPOSE 8080
COPY --from=mvn /usr/src/online-shop/target/runner/meecrowave-core-runner.jar /opt/meecrowave-runner.jar
COPY --from=mvn /usr/src/online-shop/target/customer-service.war /opt/customer-service.war
COPY --from=mvn /usr/src/online-shop/src/main/resources/log4j2.xml /opt/log4j2.xml

ENTRYPOINT ["java", "--illegal-access=permit", "-Djava.net.preferIPv4Stack=true", "-Dlog4j2.configurationFile=/opt/log4j2.xml", "-jar", "/opt/meecrowave-runner.jar", "--webapp", "/opt/customer-service.war"]
