FROM openjdk:11-jre

RUN wget https://repo.maven.apache.org/maven2/org/apache/meecrowave/meecrowave-core/1.2.13/meecrowave-core-1.2.13-runner.jar -O /opt/meecrowave-core-runner.jar
ADD target/customer-service.war /opt/customer-service.war

EXPOSE 4000
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-jar", "/opt/meecrowave-core-runner.jar", "--http", "4000", "--webapp", "/opt/customer-service.war"]
