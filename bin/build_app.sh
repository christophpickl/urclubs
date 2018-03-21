#!/usr/bin/env bash

./gradlew clean build loadUrclubsVersion createApp -Durclubs.enableMacBundle=true

#open build/macApp/

./build/macApp/UrClubs.app/Contents/MacOS/JavaAppLauncher
