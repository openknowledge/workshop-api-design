#!/bin/bash
SCRIPTS_DIR=$(readlink -f $(dirname $0))
cd $SCRIPTS_DIR

TMP_DIR=.tmp/jenkins
mkdir -p $TMP_DIR
cd $TMP_DIR

JENKINS_URL="http://localhost:30040"

COOKIE_FILE=./cookiefile
CRUMB=$(curl -c $COOKIE_FILE --silent "$JENKINS_URL/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,\":\",//crumb)")
curl -b ./cookiefile -H "$CRUMB" $*

