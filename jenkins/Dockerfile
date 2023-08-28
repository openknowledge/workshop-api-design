FROM jenkins/jenkins:2.414.1-lts-jdk11

RUN jenkins-plugin-cli -p git workflow-aggregator pipeline-utility-steps generic-webhook-trigger pipeline-stage-view

ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

USER root

# increase number of executors
COPY --chown=jenkins:jenkins executors.groovy /usr/share/jenkins/ref/init.groovy.d/executors.groovy

# install docker
RUN apt-get update -qq && apt-get install --no-install-recommends -qqy apt-transport-https ca-certificates curl gnupg2 software-properties-common\
 && curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -\
 && add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable"\
 && apt-get update -qq && apt-get install --no-install-recommends docker-ce -y\

# install kubectl
 && apt-get update -qq && apt-get install --no-install-recommends -y apt-transport-https ca-certificates curl\
 && mkdir -p /etc/apt/keyrings\
 && curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.30/deb/Release.key | gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg\
 && chmod 644 /etc/apt/keyrings/kubernetes-apt-keyring.gpg\
 && echo 'deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.30/deb/ /' | tee /etc/apt/sources.list.d/kubernetes.list\
 && apt-get update -qq && apt-get install --no-install-recommends -qy kubectl kubelet kubeadm\

# install maven
 && apt-get install --no-install-recommends -y maven\

# install pact client
 && apt-get install --no-install-recommends -y curl\
 && curl -LO https://github.com/pact-foundation/pact-ruby-standalone/releases/download/v1.69.0/pact-1.69.0-linux-x86_64.tar.gz && tar xzf pact-1.69.0-linux-x86_64.tar.gz -C /usr/local/ && ln -s /usr/local/pact/bin/pact-broker /usr/local/bin/pact-broker\
 && rm -rf /var/lib/apt/lists/*

RUN usermod -aG docker jenkins

# configure access to cluster
COPY kube-config /root/.kube/config

#USER jenkins
