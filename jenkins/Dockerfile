FROM jenkins/jenkins:2.479.1-jdk21

RUN jenkins-plugin-cli -p git workflow-aggregator pipeline-utility-steps generic-webhook-trigger pipeline-stage-view

ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

USER root

# increase number of executors
COPY --chown=jenkins:jenkins executors.groovy /usr/share/jenkins/ref/init.groovy.d/executors.groovy

RUN apt-get update -qq && apt-get install --no-install-recommends -qqy sshpass && rm -rf /var/lib/apt/lists/*

# install maven
RUN apt-get update -qq && apt-get install --no-install-recommends -y maven\

# install pact client
 && apt-get install --no-install-recommends -y curl ca-certificates\
 && apt-get install -y curl ca-certificates\
 && curl -fsSL https://deb.nodesource.com/setup_20.x | bash -\
 && apt-get install -y nodejs\
 && npm install -g @pact-foundation/pact-cli\
 && rm -rf /var/lib/apt/lists/*
