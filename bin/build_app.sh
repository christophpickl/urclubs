#!/usr/bin/env bash

./gradlew loadUrclubsVersion createApp -Durclubs.enableMacBundle=true -x test

#open build/macApp/

./build/macApp/UrClubs.app/Contents/MacOS/JavaAppLauncher
