# Workshop API Design

Herzlich willkommen zum Workshop API Design.

## Aufsetzen eines Kubernetes-Clusters mit Minikube

Wir verwenden Minikube, um einen lokalen Kubernetes-Cluster aufzusetzen.

Bitte installieren Sie [Minikube](https://kubernetes.io/de/docs/tasks/tools/install-minikube/#minikube-installieren).

Bitte starten Sie Minikube, indem sie folgenden Befehl ausführen:

```shell
minikube start --cpus 4 --memory 4096
```

## Jenkins konfigurieren

Wir möchten einen Jenkins-Server im Cluster betreiben, der in den Cluster deployen kann.
Damit das möglich ist, benötigt der Jenkins-Server die Konfiguration für den Cluster-Zugriff
Bitte legen Sie diese an, indem Sie folgenden Befehl ausführen:

```shell
kubectl config view --raw > jenkins/kube-config
```

## Bauen der Docker-Images und Laden in den Cluster

```shell
docker compose build
minikube image load host.docker.internal:5000/gogs:local
minikube image load host.docker.internal:5000/jenkins:local
minikube image load host.docker.internal:5000/setup:local
minikube image load host.docker.internal:5000/delivery-db:local
```

## Initialisierung des Clusters mit Kustomize

```shell
kubectl apply -k ./deployment/
```

## Zugriff auf den Cluster

Wenn der Cluster und die Services gestartet sind (das wird etwas dauern),
können Sie minikube so konfigurieren, dass auf alle Services zugegriffen werden kann:

```shell
minikube service --all
```

Die URLs werden dann in der Konsole ausgegeben.

Melden Sie sich beim Gogs-Service (Git-Server) mit folgenden Zugangsdaten an:

```
username: openknowledge
password: workshop
```

Für die anderen Services ist keine Authentifizierung nötig.

## Entfernen des Clusters

Um den Cluster zu entfernen, rufen Sie folgenen Befehl auf:

```shell
minikube stop
minikube delete
```
