opencv_version="4.0.1"
opencv_sdk_zip="cache/opencv-android-sdk.zip"
download_url="https://github.com/opencv/opencv/releases/download/${opencv_version}/opencv-${opencv_version}-android-sdk.zip"
mkdir cache/
wget ${download_url} -O cache/opencv-android-sdk.zip

unzip -qqo ${opencv_sdk_zip} -d opencv_sdk
cp -r opencv_sdk/OpenCV-android-sdk/sdk/* opencv/
rm -rf opencv_sdk

mkdir app/src/main/3rdparty
mkdir app/src/main/jniLibs
mkdir app/src/main/staticlibs
mkdir app/src/main/jni/include

mv opencv/native/3rdparty/* app/src/main/3rdparty
mv opencv/native/libs/* app/src/main/jniLibs
mv opencv/native/staticlibs/* app/src/main/staticlibs
mv opencv/native/jni/include/* app/src/main/jni/include
