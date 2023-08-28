#!/bin/bash

REPO_BASE_URL=gogs-service:3000

BRANCHES=("pact" "pact-broker" "pipeline" "pact-tags" "webhook")
APPS=("address-validation-service" "billing-service" "customer-service" "delivery-service")

REPO_DIR=/repo
TMP_DIR=/.tmp

cd $REPO_DIR

mkdir -p $TMP_DIR
for BRANCH in "${BRANCHES[@]}"
do
  echo $(pwd)
  git reset --hard origin/$BRANCH
  rm -rf $TMP_DIR/*
  cp -r $REPO_DIR/apps/* $TMP_DIR
  for APP in "${APPS[@]}"
  do
    (
    echo "$BRANCH -> $APP"
    cd $TMP_DIR/$APP
    echo $(pwd)
    git init --initial-branch=$BRANCH
    git config user.name openknowledge
    git config user.email "workshop@openknowledge.local"
    git remote add origin http://openknowledge:workshop@$REPO_BASE_URL/openknowledge/$APP
    git add .
    git commit -m "copy from workshop-repo branch $BRANCH"
    git push --force --set-upstream origin $BRANCH
    )
  done
done
