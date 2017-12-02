#!/usr/bin/env bash
set -e

# Doing jobs according to COMPONENT's value to minimize build times

if [ "$COMPONENT" == "BUILD" ]; then
    ./gradlew build
elif [ "$COMPONENT" == "CONNECTED_TEST" ]; then
    ./gradlew build connectedAndroidTest --stacktrace
elif [ "$COMPONENT" == "ASSEMBLE_RELEASE" ]; then
    ./gradlew assembleRelease
fi

exit 0

