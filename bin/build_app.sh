#!/usr/bin/env bash

./gradlew createApp -Durclubs.enableMacBundle=true
./build/macApp/UrClubs.app/Contents/MacOS/JavaAppLauncher
