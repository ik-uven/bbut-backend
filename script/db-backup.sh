#!/bin/bash

set -e

PASSWORD=$1

TIMESTAMP=$(date +"%m_%d_%H_%M")

OUTPUT_DIR="./backup/${TIMESTAMP}"
mkdir -p "$OUTPUT_DIR"

#echo "$PATH"
#echo "$PWD"

/usr/local/bin/mongodump --db prod -u backup_user -p "$PASSWORD" -o "$OUTPUT_DIR"

exit 0
