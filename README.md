# Workshop API Design

Herzlich willkommen zum Workshop API Design.

## Aufgabe: Design einer asynchronen API

Bitte designen Sie die asynchrone API der Rechnungserstellung.
- Immer, wenn sich eine Rechnungsadresse ändert,
wird ein fachliches Event geworfen, welches die geänderte Adresse und die Kundennummer enthält

## Verwendung des Swagger-Editors

Bitte beschreiben Sie die API im AsyncAPI-Format.
Verwenden Sie dazu den Swagger-Editor

### Starten des Swagger-Editors

Mit folgendem Befehl kann der Swagger-Editor gestartet werden:
```
docker compose up
```

### Öffnen des Swagger-Editors

Der Swagger-Editor kann über folgenden Link aufgerufen werden:
[Swagger Editor](http://localhost:6060).

## Aufgabe: Design der Konsumentenseite

Die Kundenverwaltung möchte über jede Adressänderung
von der Rechnungserstellung informiert werden.
Bitte spezifizieren Sie die Erwartung der Kundenverwaltung
als Konsument der Rechnungserstellung im AsyncAPI-Format.

