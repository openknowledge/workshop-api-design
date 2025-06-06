# Workshop API Design

Herzlich willkommen zur Übung der Verwendung von Pact in der Build- und Deployment-Pipeline

## Setup starten

Starte das Setup mit docker compose

```
docker compose up --build
```

Es werden neben unseren vier bekannten Microservices folgende Services gestartet:
- [Prometheus Server](http://localhost:4318)
- [Jaeger Server](http://localhost:5318)
- [Grafana Server](http://localhost:3000)

## Übung 1 - Finden des langsamen Services

Wir wollen den Betrieb unserer Services überwachen.
Ein besonderes Augenmerk liegt dabei auf dem Setzen der Lieferaddresse.
Dies ist ein Service-Call über mehrere Services,
der in der Vergangenheit häufiger Schwierigkeiten bereitet hat.
Der Customer Service ruft dabei den Delivery Service auf,
der wiederum den Address-Validation Service aufruft.
Wir haben mit den Stakeholdern vereinbart,
dass das Ändern der Lieferaddresse nicht länger als 100ms dauern darf.
Genauer haben wir vereinbart, dass 95 % der Requests in unter 100ms abgearbeitet sein sollen.

Betrachten wir die Beobachtung der letzten fünf Minuten,
können wir sehen, dass wir dieses Versprechen aktuell nicht einhalten.
Woran sehen wir das?

Nun wissen wir nicht, wo das Problem liegt. Es kann in allen beteiligten Services liegen,
also Customer Service, Delivery Service oder Address-Validation Service.

In welchem Service liegt das Problem?

## Übung 2 - Finden der Ursache des Performance-Problems

Was ist die Ursache dafür, dass der Service so langsam ist?

