GET http://wiremock:8080/billing-addresses/0815
Accept: application/json

HTTP 200
[Asserts]
jsonpath "$.street.streetName" == "Poststr."
jsonpath "$.street.houseNumber" == "1"
jsonpath "$.city" == "26122 Oldenburg"

GET http://wiremock:8080/billing-addresses/0815
Accept: application/de.openknowledge.sample.onlineshop.v1+json

HTTP 200
[Asserts]
jsonpath "$.street.streetName" == "Poststr."
jsonpath "$.street.houseNumber" == "1"
jsonpath "$.city" == "26122 Oldenburg"
