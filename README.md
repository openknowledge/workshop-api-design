# Workshop API Design

Herzlich willkommen zur Übung der Verwendung von Pact in der Build- und Deployment-Pipeline

## Setup starten

Starte das Setup mit docker compose

```
docker compose up --build
```

Es werden folgende Services gestartet:
- [Git Server](http://localhost:3000) (Nutzername: `openknowledge`, Password: `workshop`)
- [Jenkins](http://localhost:4000)
- [Pact Broker](http://localhost:5000)

In dem Git-Server werden während des Startups automatisch vier Projekte
für die bekannten vier Services angelegt.
Gleichzeitig starten im Jenkins automatisch Build-Prozesse, die die vier Services auf das Testsystem deployen.

Schaue im Jenkins nach, ob alle Pipelines durchgelaufen sind.

Wenn die vier Services deployed sind, kann die Swagger-UI des Customer Services auf dem Testsystem
über folgende URL erreicht werden: [Customer-Service](http://localhost:8281)

## Übung 1 - Verwendung des Pact-Brokers

Der Pact Broker ist bisher (außer eines Sample Contracts) leer. 

Wir wollen jetzt, dass die Services ihre Contracts via Pact austauschen.
Dazu muss in allen vier Services der Branch `pact-broker`
in den Branch `develop` gemerged werden.
Gehe auf den Git-Server und erstelle zunächst im `customer-service`
einen Pull-Request mit dem Base-Branch `develop`
und dem Vergleichs-Branch `pact-broker` und führe diese dann zusammen.
Die Jenkins-Pipeline sollte nun automatisch anfangen zu bauen
und den neuen Stand des Customer Services auf das Testsystem deployen.

Wenn das geschehen ist, kannst du im Pact-Broker sehen,
dass vom Customer Service zwei Contracts hochgeladen wurden,
einer für den Billing Service und einer für den Delivery Service.
Beide Contracts sind allerdings noch nicht verifiziert.

Wir mergen nun im Billing Service den Branch `pact-broker` in den Branch `develop`.
Dazu erstellen wir wieder, wie oben beschrieben, einen Pull-Request
und führen ihn zusammen.

Nachdem wir beobachtet haben, dass der Billing Service gebaut
und deployed wurde, können wir im Pact Broker sehen,
dass der Billing Service den Contract des Customer Services verifiziert hat.

Wir mergen nun analog den Delivery Service und beobachten auch hier,
dass er den Contract verifiziert.

Eine weitere Beobachtung wird sein,
dass der Delivery Service einen weiteren Contract hochgeladen hat,
nämlich für den Address-Validation Service.

Zu guter Letzt mergen wir also den Branch `pact-broker`
auch im Address-Validation Service in den Branch `develop`
und beobachten auch hier die Verifizierung.

## Übung 2 - Nur deployen, wenn der Branch verifiziert ist.

Es wäre wünschenswert, dass ein Service nur deployed wird,
wenn seine Contracts auch von den jeweiligen Providern verifiziert wurden.
Um das zu erreichen, müssen wir die Pipelines so umbauen,
dass die Consumer die Pipelines der Provider immer dann antriggern,
wenn sie einen neuen Contract hochgeladen haben.

Zunächst müssen wir allerdings die Provider-Pipelines so umbauen,
dass sie dann auch nur die Contracts verifizieren (und nicht selber deployen).
Dazu mergen wir zunächst im Address-Validation Service und im Billing Service
jeweils den Branch `pipeline` in den Branch `develop`.

Ist das geschehen (und das jeweilige Build erfolgreich durchgelaufen),
können wir zunächst im Delivery Service den Branch `pipeline` in den Branch `develop` mergen.
Danach können wir beobachten, wie der Build des Delivery Services
den Build des Address-Validation Services anstößt, dieser nur bis zur Phase Test baut
(und den Contract verifiziert) und dann der Build des Delivery Services fortgesetzt wird.

Anschließend können wir dasselbe im Customer Service beobachten,
wenn wir auch hier den Branch `pipeline` nach `develop` mergen.
Hier lässt sich beobachten, dass sowohl der Build des Billing Services
als auch der Build des Delivery Services angestoßen wird.

## Übung 3 - Verifizieren der Contracts von unterschiedlichen Stages

Um sicherzustellen, dass alle Versionen einer Stage zusammenpassen,
gibt es in Pact das Konzept der Tags.

Sobald ein Service in einer Version auf einer Stage deployed worden ist,
kann diese Version im Pact-Broker mit einem Tag der Stage versehen werden.

Wir können das sehen, indem wir sowowhl im Address-Validation Service
als auch im Billing Service jeweils den Branch `pact-tags` in den Branch `develop` mergen.
Nach erfolgreichem Deployment sind die jeweiligen Versionen im Pact Broker getagged.

Wenn wir nun im Delivery Service den Branch `pact-tags` in `develop` mergen,
können wir beobachten, dass der Address-Validation Service beim Verifizieren
des Contracts die Version verwendet, die auch auf der Stage deployed ist,
auf die der Delivery Service deployen möchte (in diesem Fall die Test-Stage).
Um zu signalisieren, dass die gerade getestete Version des Delivery Services
noch nicht auf der Stage vorhanden ist, aber auf die Stage deployed werden soll,
tagged der Delivery Service seinen Contract zunächst mit `pending-test`.
Der Address Validation Service validiert dann alle Contracts, die mit `pending-test` getagged sind.
Nach erfolgreichem Deployment des Delivery Services, tagged dieser den Contract
mit dem Tag `test`.

Dasselbe können wir erneut beim Customer Service beobachten,
wenn wir auch hier den Branch `pact-tags` in den Branch `develop` mergen.

## Übung 4 - Unabhängige Pipelines

Bisher ist es so, dass die Abhängigkeiten zwischen den Pipelines explizit
im `Jenkinsfile` eingetragen sind.
In der Praxis ist das nicht immer möglich.
Wenn Services von unterschiedlichen Teams umgesetzt werden und eventuell
sogar auf unterschiedlichen Build-Servern laufen,
werden andere Mechanismen benötigt, um die Verifikation von Contracts anzustoßen.

Der Pact Broker bietet hierzu die Möglichkeit,
zwei Arten von Web-Hooks zu registrieren.
Einerseits kann man einen Webhook registrieren,
der aufgerufen wird, wenn ein Contract hochgeladen wird.
Diesen werden wir dazu verwenden, die Verifikation anzustoßen.

Wir können die Webhooks registrieren, indem wir im Pact-Broker rechts oben
auf den `API Browser` gehen.
Dort suchen wir die Ressource `pb:webhooks` und klicken auf `Perform non-GET request`.
Das ist das gelb unterlegte `!`.

Insgesamt wollen wir fünf Webhooks anlegen: Drei zum Antriggern der Verifikation
(nämlich für den Billing Service, den Delivery Service und den Address Validation Service).
Dafür führen wir drei Requests aus mit den folgenden Bodys:

```
{
  "provider": {
    "name": "billing-service"
  },
  "events": [{
    "name": "contract_content_changed"
  }],
  "request": {
    "method": "GET",
    "url": "http://jenkins:8080/generic-webhook-trigger/invoke?token=billing-service&stage=${pactbroker.consumerVersionTags}&verifyPacts=true",
    "headers": {
    }
  }
}
```

```
{
  "provider": {
    "name": "delivery-service"
  },
  "events": [{
    "name": "contract_content_changed"
  }],
  "request": {
    "method": "GET",
    "url": "http://jenkins:8080/generic-webhook-trigger/invoke?token=delivery-service&stage=${pactbroker.consumerVersionTags}&verifyPacts=true",
    "headers": {
    }
  }
}
```

```
{
  "provider": {
    "name": "address-validation-service"
  },
  "events": [{
    "name": "contract_content_changed"
  }],
  "request": {
    "method": "GET",
    "url": "http://jenkins:8080/generic-webhook-trigger/invoke?token=address-validation-service&stage=${pactbroker.consumerVersionTags}&verifyPacts=true",
    "headers": {
    }
  }
}
```

Außerdem legen wir noch zwei Webhooks an, die getriggert werden, wenn eine Verifikation erfolgreich war
und der Service auf die Stage deployen kann. Diese brauchen wir für den Customer Service und den Delivery Service:

```
{
  "consumer": {
    "name": "customer-service"
  },
  "events": [{
    "name": "provider_verification_published"
  }],
  "request": {
    "method": "GET",
    "url": "http://jenkins:8080/generic-webhook-trigger/invoke?token=customer-service&stage=${pactbroker.consumerVersionTags}&deployOnly=true&deploymentVersion=${pactbroker.consumerVersionNumber}",
    "headers": {
    }
  }
}
```

```
{
  "consumer": {
    "name": "delivery-service"
  },
  "events": [{
    "name": "provider_verification_published"
  }],
  "request": {
    "method": "GET",
    "url": "http://jenkins:8080/generic-webhook-trigger/invoke?token=delivery-service&stage=${pactbroker.consumerVersionTags}&deployOnly=true&deploymentVersion=${pactbroker.consumerVersionNumber}",
    "headers": {
    }
  }
}
```

Wenn wir alle Webhooks angelegt haben, können wir das Ganze testen,
indem wir jeweils die Branches `webhook` in den Branch `main` mergen.

Zunächst tun wir das für den Billing Service und den Address Validation Service und warten,
bis diese auf dem Produktivsystem deployed sind.

Danach mergen wir den Branch im Delivery Service. Nun können wir beobachten,
wie der Delivery Service gebaut und gepusht wird, aber noch nicht deployed wird,
weil sein hochgeladener Contract noch nicht für die Production Stage verifiziert ist.
Das Hochladen des Contracts hat aber bereits den Address Validation Service angestoßen,
der die Verifikation vornimmt. Sobald der Address Validation Service den Contract verifiziert hat,
Wird über den Webhook wieder der Delivery Service angestoßen, der jetzt nur noch deployed.

Wenn wir im Customer Service auch den Branch `webhook` nach `main` mergen,
können wir hier dasselbe beobachten, nur dass der Customer Service die Verifikation
in Billing Service und Delivery Service anstößst und auch auf beide Verifikationen wartet.
