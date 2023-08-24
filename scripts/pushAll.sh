#!/bin/bash
SCRIPTS_DIR=$(readlink -f $(dirname $0))

TARGET_BRANCH="${1:-main}"

cd $SCRIPTS_DIR
./push.sh customer-service $TARGET_BRANCH
./push.sh billing-service $TARGET_BRANCH
./push.sh address-validation-service $TARGET_BRANCH
./push.sh delivery-service $TARGET_BRANCH

