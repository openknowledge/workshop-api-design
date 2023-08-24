FROM jenkins/jenkins:2.414.1-lts-jdk11

RUN jenkins-plugin-cli -p git workflow-aggregator pipeline-utility-steps generic-webhook-trigger pipeline-stage-view

ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

USER root

# increase number of executors
COPY --chown=jenkins:jenkins executors.groovy /usr/share/jenkins/ref/init.groovy.d/executors.groovy

RUN apt-get update -qq && apt-get install --no-install-recommends -qqy sshpass && rm -rf /var/lib/apt/lists/*

# install maven
RUN apt-get update -qq && apt-get install --no-install-recommends -y maven\

# install pact client
 && apt-get install --no-install-recommends -y curl\
 && curl -LO https://github.com/pact-foundation/pact-ruby-standalone/releases/download/v1.69.0/pact-1.69.0-linux-x86_64.tar.gz && tar xzf pact-1.69.0-linux-x86_64.tar.gz -C /usr/local/ && ln -s /usr/local/pact/bin/pact-broker /usr/local/bin/pact-broker\
 && rm -rf /var/lib/apt/lists/*
