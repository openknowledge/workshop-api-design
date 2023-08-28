FROM openjdk:11-jre

RUN wget https://repo.maven.apache.org/maven2/org/apache/meecrowave/meecrowave-core/1.2.13/meecrowave-core-1.2.13-runner.jar -O /opt/meecrowave-core-runner.jar
ADD target/billing-service.war /opt/billing-service.war

EXPOSE 4001
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-jar", "/opt/meecrowave-core-runner.jar", "--host", "0.0.0.0", "--http", "4001", "--webapp", "/opt/billing-service.war"]
