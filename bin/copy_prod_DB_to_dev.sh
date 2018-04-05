#!/usr/bin/env bash

SOURCE="$HOME/.urclubs/database"
TARGET="$HOME/.urclubs_dev/database"

echo "Copying from $SOURCE to $TARGET "

rm -rf ${TARGET}
cp -R ${SOURCE} ${TARGET}

echo "DONE"
