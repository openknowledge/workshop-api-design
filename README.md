## Workshop API Design

Herzlich willkommen zum Workshop API Design.

# Aufgabe: Rate Limiting über API Gateway

Starten Sie die Services mit Docker Compose:

```
docker compose up --build
```

## Laden aller Kunden über den API Gateway

Unter [Customers](http://localhost:8000/customers) können Sie die Kunden laden.

Wenn Sie den Reload-Button schnell
hintereinander drücken,
soll der Status Code `503 - Service temporarily unavailable` erscheinen.

Um das zu erreichen,
fügen Sie die auskommentierten Zeilen
in `api-gateway/nginx.conf` hinzu.
