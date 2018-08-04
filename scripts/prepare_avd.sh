#!/usr/bin/env bash
set -e

# Starting emulators is very costly. We should only start them if we're building a matrix
# component which requires one.

if [ "$COMPONENT" == "CONNECTED_TEST" ]; then
  echo "Starting AVD"
  echo no | android create avd --force -n test -t android-22 --abi armeabi-v7a
  emulator -avd test -no-audio -no-window &
  android-wait-for-emulator
  adb shell input keyevent 82 &
fi

exit 0

