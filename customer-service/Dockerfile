FROM maven:3.9.9-eclipse-temurin-21 AS mvn

WORKDIR /usr/src/online-shop
COPY pom.xml ./
RUN mvn package dependency:go-offline # cache dependencies
COPY src ./src
RUN mvn clean package meecrowave:bundle -Dservice.name=customer-service -Dmaven.test.skip=true -Dcheckstyle.skip=true

FROM eclipse-temurin:21-jre

RUN apt-get update -qq && apt-get install --no-install-recommends -qqy unzip && rm -rf /var/lib/apt/lists/*
COPY --from=mvn /usr/src/online-shop/target/customer-service-meecrowave-distribution.zip /opt/customer-service.zip
WORKDIR /opt
RUN unzip customer-service.zip && rm customer-service.zip
EXPOSE 8080

ENTRYPOINT ["/opt/customer-service-distribution/bin/meecrowave.sh", "run"]
