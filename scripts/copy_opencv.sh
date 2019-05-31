#!/usr/bin/env bash
mkdir app/src/main/3rdparty
rm -rf app/src/main/jni/include
mkdir app/src/main/jniLibs
mkdir app/src/main/staticlibs
mkdir app/src/main/jni/include

mv $HOME/openCV/opencv/sdk/native/3rdparty/* app/src/main/3rdparty
mv $HOME/openCV/opencv/sdk/native/libs/* app/src/main/jniLibs
mv $HOME/openCV/opencv/sdk/native/staticlibs/* app/src/main/staticlibs
mv $HOME/openCV/opencv/sdk/native/jni/include/* app/src/main/jni/include
