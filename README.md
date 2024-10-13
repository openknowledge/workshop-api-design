# Workshop API Design

Herzlich willkommen zum Workshop API Design.

Wir deployen den Cluster in Codespaces mit Minikube

Nach dem Start des Codespace muss noch folgendes getan werden:
- `skaffold run`
- `sh .devcontainers/start-minikube-service.sh`
- Mit der Url vom Ports tab in Codespaces (die url vom port 80) kann man nun auf die services zugreifen.

## Deployment in den Cluster via Skaffold

Um die Anwendungen in den Cluster zu deployen,
wird [Skaffold](https://skaffold.dev/) verwendet.

Skaffold baut die benötigten Images
und deployed sie mit Kustomize in den Cluster.

Führen Sie dazu folgenden Befehl aus:

```shell
skaffold run
```

### Known Issues beim Installieren
Hin und wieder kann es passieren, dass der Codespace zu langsam ist und skaffold eine Validierung zu früh anstößt und
dabei fehlschlägt.
Wenn der skaffold run Befehl mit einem Fehler beendet wird einfach folgende Schritte ausführen:
- `helm uninstall -n observability kube-prometheus-stack`
- `skaffold run`

## Zugriff auf den Cluster

Wenn der Cluster und die Services gestartet sind (das wird etwas dauern),
können sie über folgende URLs aufgerufen werden:

* [Pact Broker](http://localhost:30091/)
* [Backstage](http://localhost:30090/)
* [Customer Service](http://localhost:30082/webjars/swagger-ui/index.html)
* [Billing Service](http://localhost:30081/webjars/swagger-ui/index.html)
* [Delivery Service](http://localhost:30083/webjars/swagger-ui/index.html)
* [Address Validation Service](http://localhost:30080/webjars/swagger-ui/index.html)

```
For Address Validation: http://localhost:30080
For Billing: http://localhost:30081
For Customer: http://localhost:30082
For Delivery: http://localhost:30083
For Prometheus: http://localhost:30090
For Grafana: http://localhost:30030
For Jaeger: http://localhost:30091
```

## "Fixing" the missing sidecar container for OpenTelemetry

After everything has been installed on the cluster via Skaffold, you will notice that there is
only one container in each pod for the micro-services (address-validation, billing, customer, delivery).
This is because the Operator did not have the chance to boot up properly, before skaffold applied
the kustomize files to the cluster.

To get the sidecars for the pods injected, just delete the pods. The deployment will be triggered
automatically and the pods will be recreated with the sidecar containers, because the operator should
be up and running by then.

## Accessing the Grafana Dashboard

To access the Grafana Dashboard, you can use the following credentials: admin/admin
Skip the password change and you will be redirected to the dashboard.

In the Dashboard list, search for "Tracing". That is our pre-build Dashboard, which
visualizes the 4 Services with their traces, and the corresponding logs.

To actually see traffic on that dashboard, trigger some requests to the services.
For example: 
    
```shell
curl --location --request GET 'localhost:30083/delivery-addresses/0815'
```

Or to use multiple microservices to see the propagation you can change the address with a POST

```shell
curl --location --request POST 'localhost:30083/delivery-addresses/0815' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "city": "26122 Oldenburg",
        "recipient": "Max Mustermann",
        "street": {
        "name": "Musterstrasse",
        "number": "22"
    }
}'
```

Now you should see some traces in the Grafana Tracing Dashboard.

## Cleaning up the cluster

To clean up the cluster from everything that skaffold has installed, execute the following command:

```shell
skaffold delete
```

To also delete the KinD cluster and the docker container that it is running in,
execute the following commands:

```shell
docker container stop workshop-service-mngmt-cluster-control-plane
kind delete cluster -n workshop-service-mngmt-cluster
```
