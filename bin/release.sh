#!/usr/bin/env bash

VERSION_FILE="version.properties"

safeEval() {
    COMMAND=$1
    echo ""
    echo ">> $COMMAND"
    eval $COMMAND
    if [ $? -ne 0 ] ; then
        echo "Last command did not end successful!"
        exit 1
    fi
}

verifyConfirm() {
    PROMPT=$1
    echo ""
    echo -n "$PROMPT [y/n] "
    read CONFIRM_INPUT
    if [ "$CONFIRM_INPUT" != "y" ]; then
        echo "Aborted."
        exit 0
    fi
}

readNextVersionNumber() {
    VERSION_CONTENT=`cat $VERSION_FILE`
    CURRENT_VERSION="$(cut -d'=' -f2 <<<$VERSION_CONTENT)"

    NEXT_VERSION=$((CURRENT_VERSION+1))
}


readNextVersionNumber
echo "Going to release next version: $NEXT_VERSION"
echo ""
echo "Check that everything was commited..."
safeEval "git status"
verifyConfirm "Continue with release?"

echo "version=$NEXT_VERSION" > $VERSION_FILE
safeEval "./gradlew clean check checkTodo test build loadProjectVersionNumber createApp -Durclubs.production=true -Durclubs.enableMacBundle=true"
safeEval "open build/macApp/"
verifyConfirm "Are you happy with the build and want to tag this version?"

safeEval "git add ."
safeEval "git commit -m '[Auto-Release] Version: $NEXT_VERSION'"
safeEval "git tag $NEXT_VERSION"
safeEval "git push"
safeEval "git push origin --tags"
