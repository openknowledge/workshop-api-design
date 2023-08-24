#!/bin/bash
SCRIPTS_DIR=$(readlink -f $(dirname $0))
cd $SCRIPTS_DIR

JENKINS_URL="http://localhost:30070"

./jenkinsCurl.sh -X POST --data-urlencode "script@$SCRIPTS_DIR/cleanJobs.groovy" $JENKINS_URL/scriptText
