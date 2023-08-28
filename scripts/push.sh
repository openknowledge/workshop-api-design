#!/bin/bash
SCRIPTS_DIR=$(readlink -f $(dirname $0))
TMP_DIR=.tmp
APPS_DIR=$SCRIPTS_DIR/../apps

REPO_BASE_URL=localhost:30030
JENKINS_URL="http://localhost:30040"

REPO_NAME=$1
TARGET_BRANCH="${2:-main}"

# get branch name of the current repo
cd $APPS_DIR
BRANCH_NAME=$(git symbolic-ref --short HEAD)

cd $SCRIPTS_DIR/$TMP_DIR
# clean
rm -rf ./$REPO_NAME/*
rm -drf ./$REPO_NAME/.git
mkdir -p $REPO_NAME
cd ./$REPO_NAME

git init --initial-branch=$TARGET_BRANCH
git config user.name openknowledge
git remote add origin http://openknowledge:workshop@$REPO_BASE_URL/openknowledge/$REPO_NAME

cp -r $APPS_DIR/$REPO_NAME/* .
cp $APPS_DIR/$REPO_NAME/.gitignore ./

git add .
git commit -m "copy from workshop-repo branch $BRANCH_NAME"
git push --force --set-upstream origin $TARGET_BRANCH

# trigger build on jenkins right now
$SCRIPTS_DIR/jenkinsCurl.sh -X POST "$JENKINS_URL/job/$REPO_NAME/build"

