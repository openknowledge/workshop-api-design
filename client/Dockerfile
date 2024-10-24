FROM linuxserver/openssh-server:9.7_p1-r4-ls171
RUN apk --update add sshpass && rm -rf /var/cache/apk/*
ENTRYPOINT sleep 5 && sshpass -p jenkins ssh -o StrictHostKeyChecking=no -p 2222 jenkins@server.local "java -version"
#RUN wget -O /etc/apk/keys/adoptium.rsa.pub https://packages.adoptium.net/artifactory/api/security/keypair/public/repositories/apk\
#  && echo 'https://packages.adoptium.net/artifactory/apk/alpine/main' >> /etc/apk/repositories\
#  && apk add -q --update --no-cache temurin-21-jdk
#FROM eclipse-temurin:17-alpine
#RUN apk --update add openssh && rm -rf /var/cache/apk/*
#RUN printf 'PathwordAuthentication yes\nPermitEmptyPassword yes' >> /etc/ssh/sshd_config
#RUN rc-update add sshd
#RUN rc-service ssh start
#ENTRYPOINT /etc/init.d/ssh start
#ENTRYPOINT cat /etc/ssh/sshd_config
#EXPOSE 22
#ENTRYPOINT tail -f /dev/null
#ENTRYPOINT ls -Al /etc/ssh
