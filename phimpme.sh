mkdir $HOME/openCV/
cd $HOME/openCV
wget https://github.com/opencv/opencv/releases/download/4.0.1/opencv-4.0.1-android-sdk.zip

unzip opencv-4.0.1-android-sdk.zip
rm -rf opencv-4.0.1-android-sdk.zip
# Rename folder name to opencv
mv OpenCV-android-sdk opencv

cd $TRAVIS_BUILD_DIR

mkdir app/src/main/3rdparty
mkdir app/src/main/jniLibs
mkdir app/src/main/staticlibs
mkdir app/src/main/jni/include

mv $HOME/openCV/opencv/sdk/native/3rdparty/* app/src/main/3rdparty
mv $HOME/openCV/opencv/sdk/native/libs/* app/src/main/jniLibs
mv $HOME/openCV/opencv/sdk/native/staticlibs/* app/src/main/staticlibs
mv $HOME/openCV/opencv/sdk/native/jni/include/* app/src/main/jni/include
