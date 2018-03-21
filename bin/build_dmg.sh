#!/usr/bin/env bash

./gradlew clean build loadUrclubsVersion createDmg -Durclubs.enableMacBundle=true

open build/distributions
