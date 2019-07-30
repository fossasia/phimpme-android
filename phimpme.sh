mkdir openCV/
cd openCV
wget https://github.com/opencv/opencv/releases/download/4.0.1/opencv-4.0.1-android-sdk.zip

unzip opencv-4.0.1-android-sdk.zip
rm -rf opencv-4.0.1-android-sdk.zip
# Rename folder name to opencv
mv OpenCV-android-sdk opencv

cd ..

mkdir phimpme-android/app/src/main/3rdparty
mkdir phimpme-android/app/src/main/jniLibs
mkdir phimpme-android/app/src/main/staticlibs
mkdir phimpme-android/app/src/main/jni/include

mv openCV/opencv/sdk/native/3rdparty/* phimpme-android/app/src/main/3rdparty
mv openCV/opencv/sdk/native/libs/* phimpme-android/app/src/main/jniLibs
mv openCV/opencv/sdk/native/staticlibs/* phimpme-android/app/src/main/staticlibs
mv openCV/opencv/sdk/native/jni/include/* phimpme-android/app/src/main/jni/include
