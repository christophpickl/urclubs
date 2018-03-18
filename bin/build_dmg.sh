#!/usr/bin/env bash

./gradlew createDmg -Durclubs.enableMacBundle=true

open build/distributions
