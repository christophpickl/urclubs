#!/usr/bin/env bash

ODB_PATH="/Applications/objectdb-2.7.4_01/bin"

echo "Starting ObjectDB explorer located at: $ODB_PATH"

exec java -cp "${ODB_PATH}/objectdb.jar:${ODB_PATH}/explorer.jar" com.objectdb.Explorer
