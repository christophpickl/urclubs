#!/usr/bin/env bash

SOURCE="/Users/wu/.urclubs/database"
TARGET="/Users/wu/.urclubs_dev/database"

echo "Copying from $SOURCE to $TARGET "

rm -rf ${TARGET}
cp -R ${SOURCE} ${TARGET}

echo "DONE"
