#!/bin/sh

JENKINS_URL="http://jenkins-service:8080"
# JENKINS_URL="http://localhost:30070" # for local testing

#wait until jenkins is ready
curl --retry 100  -f  --retry-all-errors  --retry-delay 1 --silent "$JENKINS_URL/crumbIssuer/api/xml" >> /dev/null

COOKIE_FILE=./cookiefile
CRUMB=$(curl -c $COOKIE_FILE --silent "$JENKINS_URL/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,\":\",//crumb)")

createjob()
{
  local NAME=$1
  if curl --fail --silent "$JENKINS_URL/job/$NAME"; then # check if job already exists
    curl -b ./cookiefile -X POST -H "$CRUMB" "$JENKINS_URL/job/$NAME/doDelete" # delete old job
    echo old job for $NAME was removed
  fi
  curl -b ./cookiefile -X POST -H "Content-Type:application/xml" -H "$CRUMB" -d @$NAME/config.xml "$JENKINS_URL/createItem?name=$NAME"
  echo pipeline for $NAME created
}

createjob customer-service
createjob address-validation-service
createjob billing-service
createjob delivery-service

rm $COOKIE_FILE