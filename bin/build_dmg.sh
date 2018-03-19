#!/usr/bin/env bash

./gradlew clean build loadProjectVersionNumber createDmg -Durclubs.enableMacBundle=true

open build/distributions
