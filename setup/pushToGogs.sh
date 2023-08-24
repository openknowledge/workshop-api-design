#!/bin/bash

REPO_BASE_URL=gogs:3000

BRANCHES=("pact" "pact-broker" "pipeline" "pact-tags" "webhook")
APPS=("address-validation-service" "billing-service" "customer-service" "delivery-service")

REPO_DIR=/repo
TMP_DIR=/.tmp

cd $REPO_DIR
git checkout -- .

for APP in "${APPS[@]}"
do
  (
  echo "main -> $APP"
  mkdir -p $TMP_DIR/$APP
  cd $TMP_DIR/$APP
  echo $(pwd)
  echo "$APP" > README.md
  git init --initial-branch=main
  git config user.name openknowledge
  git config user.email "workshop@openknowledge.local"
  git remote add origin http://openknowledge:workshop@$REPO_BASE_URL/openknowledge/$APP
  git add .
  git commit -m "Initial commit"
  git push --force --set-upstream origin main
  )
done

for BRANCH in "${BRANCHES[@]}"
do
  cd $REPO_DIR
  echo $(pwd)
  git checkout $BRANCH
  for APP in "${APPS[@]}"
  do
    (
    cd $TMP_DIR/$APP
    git checkout -b $BRANCH    
    cp -r $REPO_DIR/$APP/* .
    echo "$BRANCH -> $APP"
    echo $(pwd)
    git add .
    git commit -m "Create branch $BRANCH"
    git push --set-upstream origin $BRANCH
    )
  done
done

echo "Creating 'main' and 'develop' branches"
for APP in "${APPS[@]}"
do
  (
  cd $TMP_DIR/$APP
  git checkout pact
  git checkout -b develop
  git push --set-upstream origin develop
  git checkout main
  git merge develop
  git push
  ) 
done
