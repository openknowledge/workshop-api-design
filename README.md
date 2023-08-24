# Workshop API Design

Herzlich willkommen zum Workshop API Design.

## Aufsetzen eines Kubernetes-Clusters mit KinD

Wir verwenden KinD, um einen lokalen Kubernetes-Cluster aufzusetzen

Bitte installieren Sie [KinD](https://kind.sigs.k8s.io/docs/user/quick-start).

Bitte legen Sie einen Cluster an, indem Sie folgenden Befehl ausführen:

```shell
kind create cluster --config=./deployment/cluster-config/kind-config.yml --name=workshop-cdc-cluster
```

## Jenkins konfigurieren

Wir möchten einen Jenkins-Server im Cluster betreiben, der in den Cluster deployen kann.
Damit das möglich ist, benötigt der Jenkins-Server die Konfiguration für den Cluster-Zugriff
Bitte legen Sie diese an, indem Sie folgenden Befehl ausführen:

```shell
kind -n workshop-cdc-cluster get kubeconfig --internal > jenkins/kube-config
```

## Bauen der Docker-Images und Laden in den Cluster

```shell
docker compose build
kind load docker-image host.docker.internal:5000/gogs:local -n workshop-cdc-cluster
kind load docker-image host.docker.internal:5000/jenkins:local -n workshop-cdc-cluster
kind load docker-image host.docker.internal:5000/setup:local -n workshop-cdc-cluster
kind load docker-image host.docker.internal:5000/delivery-db:local -n workshop-cdc-cluster
```

## Initialisierung des Clusters mit Kustomize

```shell
kubectl apply -k ./deployment/
```

## Zugriff auf den Cluster

Wenn der Cluster und die Services gestartet sind (das wird etwas dauern),
können sie über folgende URLs aufgerufen werden:

* [Pact Broker](http://localhost:30020/)
* [Gogs (Git)](http://localhost:30030/)
* [Jenkins](http://localhost:30040/)

## Entfernen des Clusters

Um den Cluster zu entfernen, rufen Sie folgenen Befehl auf:

```shell
kind delete cluster -n workshop-cdc-cluster
```
