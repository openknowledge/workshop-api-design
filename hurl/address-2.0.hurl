GET http://wiremock:8080/billing-addresses/0815
Accept: application/de.openknowledge.sample.onlineshop.v2+json

HTTP 200
[Asserts]
jsonpath "$.addressLine1" == "Poststr. 1"
jsonpath "$.addressLine2" == ""
jsonpath "$.location.zipCode" == "26122"
jsonpath "$.location.cityName" == "Oldenburg"
jsonpath "$.street" not exists
jsonpath "$.city" not exists
