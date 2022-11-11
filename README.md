# Workshop API Design
   
Herzlich willkommen zum Workshop API Design.
   
## Aufgabe: Implementierung des Authorization Code Flows

Starten Sie die Services mit Docker Compose:

```
docker compose up --build
```

Unter [Startseite](http://localhost:6060/index.html) finden Sie die Startseite.
Der Login-Button verwendet allerdings noch kein PKCE.

Um PKCE zu verwenden, müssen sie dem Authorization-Request zwei Parameter hinzufügen:

```
code_challenge_method: "S256"
code_challenge: <<Der Code Challenge>>
```

Um den Code Challenge zu erstellen, müssen Sie zunächst einen zufälligen String erzeugen,
den sogenannten Code Verifier.
Den Code Verifier müssen Sie sich lokal (z.B. im Session-Storage) speichern.
Darüber wird eine Man-In-The-Middle-Attacke verhindert.
Indem Sie der Methode `generateCodeChallenge` diesen zufälligen String übergeben,
erhalten sie den passenden Code Challenge.

Später müssen Sie beim Token-Request einen Parameter namens `code_verifier` hinzufügen,
der den gespeicherten Code Verifier enthält. Nur wenn dieser zum Code Challenge passt,
erhalten Sie dann ein JWT.
