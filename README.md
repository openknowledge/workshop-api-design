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
kann das Grafana-Dashboard über foldenden Befehl zur Verfügung gestellt werden:

```
minikube service -n observability kube-prometheus-stack-grafana --url
```

Danach muss die ausgegebene URL in der Nginx-Konfig unter `gateway/nginx.conf` eingetragen werden.
Der Gateway kann nun mit `docker compose up` gestartet werden.

Auf das Grafana-Dashboard kann über folgenden Link zugegriffen werden:

[http://localhost:9090/grafana](http://localhost:9090/grafana)

Die Login-Daten sind admin/admin.

