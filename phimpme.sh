wget https://github.com/opencv/opencv/releases/download/4.0.1/opencv-4.0.1-android-sdk.zip

unzip -qqo opencv-4.0.1-android-sdk.zip
rm -rf opencv-4.0.1-android-sdk.zip


mkdir app/src/main/3rdparty
mkdir app/src/main/jniLibs
mkdir app/src/main/staticlibs
mkdir app/src/main/jni/include

mv OpenCV-android-sdk/sdk/native/3rdparty/* app/src/main/3rdparty
mv OpenCV-android-sdk/sdk/native/libs/* app/src/main/jniLibs
mv OpenCV-android-sdk/sdk/native/staticlibs/* app/src/main/staticlibs
mv OpenCV-android-sdk/sdk/native/jni/include/* app/src/main/jni/include
