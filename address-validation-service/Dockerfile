FROM maven:3.9.9-eclipse-temurin-21 AS mvn

WORKDIR /usr/src/online-shop
COPY pom.xml ./
RUN mvn package dependency:go-offline # cache dependencies
COPY src ./src
RUN mvn clean package meecrowave:bundle -Dservice.name=address-validation-service -Dmaven.test.skip=true -Dcheckstyle.skip=true

FROM eclipse-temurin:21-jre

RUN apt-get update -qq && apt-get install --no-install-recommends -qqy unzip && rm -rf /var/lib/apt/lists/*
COPY --from=mvn /usr/src/online-shop/target/address-validation-service-meecrowave-distribution.zip /opt/address-validation-service.zip
WORKDIR /opt
RUN unzip address-validation-service.zip && rm address-validation-service.zip
EXPOSE 8080

ENTRYPOINT ["/opt/address-validation-service-distribution/bin/meecrowave.sh", "run"]
