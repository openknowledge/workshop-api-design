# Workshop API Design

Herzlich willkommen zum Workshop API Design.

## Aufsetzen eines Kubernetes-Clusters mit KinD

Wir verwenden KinD, um einen lokalen Kubernetes-Cluster aufzusetzen

Bitte installieren Sie [KinD](https://kind.sigs.k8s.io/docs/user/quick-start).

Bitte legen Sie einen Cluster an, indem Sie folgenden Befehl ausführen:

```shell
kind create cluster --config=./deployment/cluster-setup/kind-config.yml --name=workshop-service-mngmt-cluster
```

Überprüfen, dass der Kontext auf
workshop-service-mgmt-cluster gesetzt ist:

```shell
kubectl config current-context
```

Wenn der Kontext nicht automatisch gesetzt wurde,
der Cluster aber läuft,
kann der Kontext manuell gesetzt werden:

```shell
kubectl config set-context kind-workshop-service-mgmt-cluster
```
## Deployment in den Cluster via Skaffold

Um die Anwendungen in den Cluster zu deployen,
wird [Skaffold](https://skaffold.dev/) verwendet.

Skaffold baut die benötigten Images
und deployed sie mit Kustomize in den Cluster.

Führen Sie dazu folgenden Befehl aus:

```shell
skaffold run
```

### Known Issues mit dem Ingress Operator

Es kann passieren,
dass der ingress-operator nicht schnell genug installiert wird.
Bei einem Fehler führen Sie einfach
den Skaffold-Befehl erneut aus.

## Zugriff auf den Cluster

Wenn der Cluster und die Services gestartet sind (das wird etwas dauern),
können sie über folgende URLs aufgerufen werden:

* [Pact Broker](http://localhost:30091/)
* [Backstage](http://localhost:30090/)
* [Customer Service](http://localhost:30082/webjars/swagger-ui/index.html)
* [Billing Service](http://localhost:30081/webjars/swagger-ui/index.html)
* [Delivery Service](http://localhost:30083/webjars/swagger-ui/index.html)
* [Address Validation Service](http://localhost:30080/webjars/swagger-ui/index.html)

## Entfernen des Clusters

Um den Cluster zu entfernen, rufen Sie folgenen Befehl auf:

```shell
kind delete cluster -n workshop-service-mngmt-cluster
```
