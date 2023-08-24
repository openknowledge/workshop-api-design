FROM linuxserver/openssh-server:9.7_p1-r4-ls171
RUN wget -O /etc/apk/keys/adoptium.rsa.pub https://packages.adoptium.net/artifactory/api/security/keypair/public/repositories/apk\
  && echo 'https://packages.adoptium.net/artifactory/apk/alpine/main' >> /etc/apk/repositories\
  && apk add -q --update --no-cache temurin-21-jdk
RUN mkdir -p /config/address-validation-service-distribution/bin\
 && touch /config/address-validation-service-distribution/bin/meecrowave.sh\
 && chmod +x /config/address-validation-service-distribution/bin/meecrowave.sh
EXPOSE 2222 8081 8082 8083 8084
