#!/usr/bin/env bash

ROOT_DEV="$HOME/.urclubs_dev"

echo "Deleting ..."
echo ""

echo "rm -rf $ROOT_DEV/database"
rm -rf ${ROOT_DEV}/database

echo "rm -rf $ROOT_DEV/cache"
rm -rf ${ROOT_DEV}/cache

echo ""
echo "Done."
