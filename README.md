# Workshop API Design
   
Herzlich willkommen zum Workshop API Design.
   
## Aufgabe: Authentifizierung mit JWT

Starten Sie die Services mit Docker Compose:

```
docker compose up --build
```

Unter [Customer Service](http://localhost:4000/webjars/swagger-ui/index.html)
erreichen Sie die Swagger UI des Customer Service.

Wenn Sie dort versuchen, sich die Liste aller Kunden ausgeben zu lassen,
werden Sie feststellen, dass Sie dies nicht dürfen.

### Holen eines Json Web Token

Mit dem folgenden HTTP-Aufruf können Sie ein Token erhalten:
```
POST http://localhost:9191/realms/master/protocol/openid-connect/token
```
Header:
```
Content-Type: application/x-www-form-urlencoded
```
Body:
```
grant_type:password
client_id:onlineshop
username:erika
password:erika123
```

Folgende Benutzer stehen zur Verfügung:

* admin / admin123 (role admin)
* erika / erika123 (role user)
* max / max123 (role user)
* james / james123 (role user)

Sie können die Authentifizierung auch über die
[Swagger UI des Authentication Service](http://localhost:6060/)
durchführen.

### Analysieren des Tokenn

Das erhaltene Token ist base64-codiert.
Man kann es sich unter [JWT.io](https://jwt.io) anschauen.

### Aufruf des Services

Das erhaltene JWT können Sie zur Authentifizierung beim
[Customer Service](http://localhost:4000/webjars/swagger-ui/index.html)
verwenden.

### Authorisierung

Sie werden feststellen, dass sie auch mit dem Benutzer `erika`
nicht alle Kunden sehen dürfen.
Sie dürfen aber Kundendetails von `erika` (Kundennummer `0816`) sehen.
Was müssen Sie tun, um alle Kunden abrufen zu können?
Dürfen Sie auch die Details von Max Mustermann (Kundennummer `0815` sehen?
Was ist das Problem?
