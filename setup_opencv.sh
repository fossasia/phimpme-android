opencv_version="4.0.1"
opencv_sdk_zip="cache/opencv-android-sdk.zip"
download_url="https://github.com/opencv/opencv/releases/download/${opencv_version}/opencv-${opencv_version}-android-sdk.zip"
mkdir -p cache/
wget ${download_url} -c -O cache/opencv-android-sdk.zip

unzip -qqo ${opencv_sdk_zip} -d opencv_sdk

mkdir app/src/main/3rdparty
mkdir app/src/main/jniLibs
mkdir app/src/main/staticlibs
mkdir app/src/main/jni/include

mv opencv_sdk/OpenCV-android-sdk/sdk/native/3rdparty/* app/src/main/3rdparty
mv opencv_sdk/OpenCV-android-sdk/sdk/native/libs/* app/src/main/jniLibs
mv opencv_sdk/OpenCV-android-sdk/sdk/native/staticlibs/* app/src/main/staticlibs
mv opencv_sdk/OpenCV-android-sdk/sdk/native/jni/include/* app/src/main/jni/include
