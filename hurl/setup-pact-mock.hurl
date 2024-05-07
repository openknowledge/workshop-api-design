DELETE http://pact-mock-server:1234/interactions
X-Pact-Mock-Service: true

POST http://pact-mock-server:1234/interactions
X-Pact-Mock-Service: true
{
  "description": "A request to get all customers",
  "consumer": "customer-client",
  "provider": "customer-service",
  "request": {
    "path": "/customers",
    "method": "GET"
  },
  "response": {
    "status": 200,
    "headers": {
      "Content-Type": "application/json"
    },
    "body": [
      {
        "name": "Max Mustermann",
        "number": "0815"
      },
      {
        "name": "Erika Mustermann",
        "number": "0816"
      },
      {
        "name": "James Bond",
        "number": "007"
      }
    ]
  }
}
