# Workshop API Design

Herzlich willkommen zum Workshop API Design.

## Übungen

### API Design

- [OpenAPI](https://github.com/openknowledge/workshop-api-design/tree/openapi)
- [Mocking](https://github.com/openknowledge/workshop-api-design/tree/wiremock)
- [AsyncAPI](https://github.com/openknowledge/workshop-api-design/tree/asyncapi)

### API Testing

- [Pact](https://github.com/openknowledge/workshop-api-design/tree/pact-mock-server)
- [Pact Pipeline](https://github.com/openknowledge/workshop-api-design/tree/pact)

## Übung: Von Playwright-Mocking zu Pact Consumer Tests

### Ausgangssituation

Im Branch `playwright-pact` befinden sich drei Playwright-Testdateien im Verzeichnis `customer-client/test/`:

- `customer-list.spec.ts` – Tests für die Kundenliste
- `customer-detail.spec.ts` – Tests für die Kundendetailansicht inkl. Adressen
- `create-customer.spec.ts` – Tests für das Anlegen eines neuen Kunden (**bereits auf Pact umgebaut, dient als Referenz**)

Die Tests in `customer-list.spec.ts` und `customer-detail.spec.ts` verwenden aktuell Playwright's eingebautes `page.route()`, um API-Aufrufe direkt im Browser abzufangen und mit statischen Antworten zu mocken. Dieser Ansatz dokumentiert den API-Vertrag nicht maschinenlesbar und kann nicht automatisch gegen den echten Service verifiziert werden.

Das Hilfsmuster `pact-proxy.ts` stellt zwei Funktionen bereit:
- `createProvider()` – erstellt einen Pact-Mock-Server mit Consumer- und Providernamen
- `setupApiProxy(page, mockServerUrl)` – leitet alle API-Aufrufe der Seite transparent an den Pact-Mock-Server weiter

### Aufgabe

Baue die Playwright-Tests in `customer-list.spec.ts` und `customer-detail.spec.ts` so um, dass sie statt `page.route()` den Pact-Mock-Server nutzen. Orientiere dich dabei an `create-customer.spec.ts`.

Für jeden Test, der API-Aufrufe mockt, gilt folgendes Muster:

```typescript
await createProvider()
  .addInteraction({
    uponReceiving: 'Beschreibung der Anfrage',
    withRequest: {
      method: 'GET',
      path: '/customers/',
    },
    willRespondWith: {
      status: 200,
      headers: { 'Content-Type': 'application/json' },
      body: [ /* ... */ ],
    },
  })
  .executeTest(async (mockServer) => {
    await setupApiProxy(page, mockServer.url);
    // eigentlicher Test
  });
```

**Hinweise:**
- Tests, die keinerlei API-Aufrufe machen (z. B. reine UI-Validierungen), müssen nicht umgebaut werden.
- Wenn ein Test mehrere API-Endpunkte anspricht, können mehrere `.addInteraction()`-Aufrufe verkettet werden.
- Nach dem Umbau werden die Pact-Dateien automatisch im Verzeichnis `pacts/` erzeugt. Prüfe, ob die erzeugten Pacts den erwarteten Vertrag korrekt abbilden.
- `beforeEach`-Blöcke mit `page.route()` müssen in die jeweiligen `executeTest`-Callbacks verschoben werden, weil `setupApiProxy` erst innerhalb des Callbacks aufgerufen werden kann.

### Ziel

Nach dem Umbau laufen alle Tests weiterhin erfolgreich, und im Verzeichnis `pacts/` liegt eine aktualisierte JSON-Datei mit den Consumer-Driven Contracts, die später gegen den echten Customer Service verifiziert werden können.

