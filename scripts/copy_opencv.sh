#!/usr/bin/env bash
mkdir app/src/main/3rdparty
mkdir app/src/main/jniLibs

mv $HOME/openCV/opencv/sdk/native/3rdparty/* app/src/main/3rdparty
mv $HOME/openCV/opencv/sdk/native/libs/* app/src/main/jniLibs
