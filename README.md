# Workshop API Design
   
Herzlich willkommen zum Workshop API Design.
   
## Aufgabe: Implementierung des Authorization Code Flows

Starten Sie die Services mit Docker Compose:

```
docker compose up --build
```

Unter [Startseite](http://localhost:6060/index.html) finden Sie die Startseite.
Der Login-Button ist allerdings noch funktionslos.

Sie können die Startseite ändern, indem Sie `authentication-ui/static/index.html`
bearbeiten.

Beim Klick auf den Login-Button soll ein Redirect auf den Authorization-Endpoint
`http://localhost:9191/realms/master/protocol/openid-connect/auth` erfolgen.
Dabei müssen folgende Query-Parameter übergeben werden:

```
client_id: "onlineshop"
scope: "openid"
response_type: "code"
redirect_uri: <die eigene Seite>
```

Wenn das Login erfolgreich war,
wird der Authorization-Server den Redirect ausführen
und einen Query-Parameter namens `code` mitschicken.
Dies ist der Authorization-Code.

Mit dem Authorization Code kann nun ein POST-Request auf den Token-Endpoint
`http://localhost:9191/realms/master/protocol/openid-connect/token`
erfolgen.
Der POST-Request wird mit dem Content-Type `application/x-www-form-urlencoded` ausgeführt.
Dabei wird folgender Body mitgeschickt:

```
client_id: "onlineshop"
scope: "openid"
grant_type: "authorization_code"
redirect_uri: <die eigene Seite>
code: <<der Authorization Code>>
```

In der Antwort dieses Requests ist das JWT enthalten.
Dieses kann nun auf der Startseite angezeigt werden.
