#!/bin/bash

# List of your NodePort service names (hardcoded)
services=(
  "service1"
  "service2"
  "service3"
  # Add more services as needed
)

# Loop through the list of services and print the URL for each
for service in "${services[@]}"; do
  echo "Fetching URL for $service:"
  minikube service "$service" --url
  echo ""
done