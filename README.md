# Workshop API Design

Herzlich willkommen zum Workshop API Design.

# Aufgabe: Developers Portal mit Backstage

Starten Sie die Services mit Docker Compose:

```
docker compose up --build
```

## Zugriff auf die Services

Wenn der Cluster und die Services gestartet sind (das wird etwas dauern),
können sie über folgende URLs aufgerufen werden:

* [Pact Broker](http://localhost:9292/)
* [Backstage](http://localhost:7007/)
* [Customer Service](http://localhost:4000/webjars/swagger-ui/index.html)
* [Billing Service](http://localhost:4001/webjars/swagger-ui/index.html)
* [Delivery Service](http://localhost:4002/webjars/swagger-ui/index.html)
* [Address Validation Service](http://localhost:4003/webjars/swagger-ui/index.html)

## API-Layers

Welche API befindet sich in welchem Layer?

## API Depencencies

Deployen Sie die Consumer-Contracts der APIs zum Pact-Broker
und beobachen Sie, wie die API-Dependencies in Backstage übernommen werden.

Führen Sie dazu in den Ordnern `delivery-service` und `customer-service` folgenden Maven-Befehl aus:

```
mvn pact:publish
```

Falls Sie auf Ihrem System kein Maven installiert haben, führen Sie bitte folgenden Befehl aus:

``` 
docker run -it -v ~/.m2:/root/.m2 -v "$(PWD)":"/usr/src/mymaven" -w /usr/src/mymaven maven mvn pact:publish -Dpact.url=http://host.docker.internal:9292
```
 
