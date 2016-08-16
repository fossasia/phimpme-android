./gradlew build jacocoTestReport assembleAndroidTest
echo no | android create avd --force -n test -t google_apis-$ANDROID_API_LEVEL --abi armeabi-v7a
emulator -avd test -no-skin -no-audio -no-window &
android-wait-for-emulator
adb shell setprop dalvik.vm.dexopt-flags v=n,o=v
./gradlew connectedCheck