FROM openjdk:11-jre

RUN wget https://repo.maven.apache.org/maven2/javax/xml/bind/jaxb-api/2.3.1/jaxb-api-2.3.1.jar -O /opt/jaxb-api.jar
RUN wget https://repo.maven.apache.org/maven2/org/apache/meecrowave/meecrowave-core/1.2.13/meecrowave-core-1.2.13-runner.jar -O /opt/meecrowave-core-runner.jar
ADD target/delivery-service.war /opt/delivery-service.war

EXPOSE 4002
ENTRYPOINT ["java", "--illegal-access=permit", "-Djava.net.preferIPv4Stack=true", "-cp", "/opt/meecrowave-core-runner.jar:/opt/jaxb-api.jar", "org.apache.meecrowave.runner.Cli", "--http", "4002", "--webapp","/opt/delivery-service.war"]
