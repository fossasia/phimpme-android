./gradlew build jacocoTestReport assembleAndroidTest
adb shell setprop dalvik.vm.dexopt-flags v=n,o=v
./gradlew connectedCheck