# cdc-delivery-service

Delivery Service to show Consumer-Driven Contracts

## Installing the database

Please run the following commands:
```
docker build -t delivery-db -f Postgres-Dockerfile .
docker tag delivery-db localhost:5000/delivery-db:1.0.0
docker tag delivery-db localhost:5000/delivery-db:latest
docker push localhost:5000/delivery-db:1.0.0
docker push localhost:5000/delivery-db:latest

cd helm-database/delivery-db/
helm package .
helm install delivery-db --namespace onlineshop .
helm install delivery-db --namespace onlineshop-test .
