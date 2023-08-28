FROM alpine/git:2.40.1

RUN apk --no-cache add curl bash
COPY .git /repo/.git
COPY setup/setup.sh /
COPY setup/pushToGogs.sh /
COPY setup/jenkins/ /jenkins

ENTRYPOINT ["sh", "/setup.sh"]
