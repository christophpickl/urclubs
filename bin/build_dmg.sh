#!/usr/bin/env bash

./gradlew loadProjectVersionNumber createDmg -Durclubs.enableMacBundle=true

open build/distributions
