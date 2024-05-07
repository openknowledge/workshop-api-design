GET http://wiremock:8080/customers

HTTP 200
[Asserts]
jsonpath "$[0].name" == "Max Mustermann"
jsonpath "$[1].name" == "Erika Mustermann"
jsonpath "$[2].name" == "James Bond"

