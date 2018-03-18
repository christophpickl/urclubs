#!/usr/bin/env bash

./gradlew loadProjectVersionNumber createApp -Durclubs.enableMacBundle=true

#open build/macApp/

./build/macApp/UrClubs.app/Contents/MacOS/JavaAppLauncher
