# Workshop API Design

Herzlich willkommen zum Workshop API Design.

## Übung: Playwright Tests mit WireMock

In dieser Übung werden die Playwright-Tests so angepasst, dass sie nicht mehr gegen den echten Backend-Service laufen, sondern alle HTTP-Requests über WireMock gemockt werden.

### Ausgangssituation

Im Verzeichnis `customer-client/test/` befinden sich drei Testdateien:

- `customer-list.spec.ts` – Tests für die Kundenliste
- `customer-detail.spec.ts` – Tests für die Kundendetailseite (Adressen anlegen und bearbeiten)
- `create-customer.spec.ts` – Tests für das Anlegen neuer Kunden

Die Tests laufen aktuell gegen den echten Backend-Service (`http://localhost:8181`).
Im Verzeichnis `wiremock/mappings/` liegen bereits zwei WireMock-Mappings:

- `get-customers.json` – Mockt `GET /customers/`
- `options-customers.json` – Mockt `OPTIONS /customers/`

WireMock wird per Docker Compose gestartet und ist auf Port `8080` erreichbar:

```bash
docker compose up wiremock
```

### Ziel

Alle HTTP-Requests in den Playwright-Tests sollen durch WireMock-Mappings ersetzt werden, sodass der echte Backend-Service für die Tests nicht mehr benötigt wird.

### Aufgaben

1. **Playwright auf WireMock umstellen**
   Ändere in `customer-client/playwright.config.ts` die `VITE_API_URL` so, dass sie auf den WireMock-Server (`http://localhost:8080`) zeigt.

2. **WireMock-Mappings für die Kundendetailseite anlegen**
   Für die Tests in `customer-detail.spec.ts` werden Kundendaten für `0815` (Max Mustermann) und `007` (James Bond) benötigt. Lege entsprechende Mappings an:
   - `GET /customers/0815` – Max Mustermann mit Rechnungs- und Lieferadresse
   - `GET /customers/007` – James Bond ohne Adressen
   - `PUT /customers/0815/billing-address` – Rechnungsadresse speichern
   - `PUT /customers/0815/delivery-address` – Lieferadresse speichern
   - Adressvalidierung (PLZ-Prüfung) für die entsprechenden Testszenarien

3. **WireMock-Mapping für das Anlegen neuer Kunden anlegen**
   Für `create-customer.spec.ts` wird ein `POST /customers/` benötigt, der einen neuen Kunden anlegt.

4. **Alle Tests erfolgreich ausführen**
   Starte WireMock per Docker Compose und führe die Tests aus:
   ```bash
   docker compose up wiremock
   cd customer-client
   npm test
   ```
   Alternativ kann auch `npm test:ui` verwendet werden, um sich die UI-Interaktionen in der Playwright UI anzusehen.

### Tipps

- WireMock-Mappings liegen als JSON-Dateien in `wiremock/mappings/`. WireMock lädt diese Dateien beim Start automatisch.
- Die bestehenden Mappings (`get-customers.json`, `options-customers.json`) können als Vorlage für neue Mappings genutzt werden.
- Für das Laden neuer Mappings ohne Neustart kann die WireMock Admin API unter `http://localhost:8080/__admin/mappings` genutzt – oder der WireMock-Container einfach neu gestartet werden.
- Bei einem `GET`, `POST`- oder `PUT`-Request muss ggf. auch der entsprechende `OPTIONS`-Preflight-Request gemockt werden (CORS).
- Die Adressvalidierung in `customer-detail.spec.ts` erwartet spezifische Fehlermeldungen – die Responses im Mapping müssen die exakten Fehlertexte aus den Tests zurückliefern.
