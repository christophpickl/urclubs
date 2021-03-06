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
        exit 4
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

echo "version=$NEXT_VERSION" > ${VERSION_FILE}

BUILD_COMMAND="./gradlew clean checkTodo test systemTest check build loadUrclubsVersion createDmg -Durclubs.environment=prod -Durclubs.enableMacBundle=true"
echo ""
echo ">> $BUILD_COMMAND"
eval ${BUILD_COMMAND}
if [ $? -ne 0 ] ; then
echo "version=$CURRENT_VERSION" > ${VERSION_FILE}
    echo "Gradle build failed!"
    exit 1
fi

safeEval "git add ."
safeEval "git commit -m '[Auto-Release] Version: $NEXT_VERSION'"
safeEval "git tag $NEXT_VERSION"
safeEval "git push"
safeEval "git push origin --tags"

exit 0
