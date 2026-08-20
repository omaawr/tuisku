### Contributing

Before contributing to Tuisku, it's recommended that you install a JDK (like Temurin or OpenJDK) and also the Android SDK, both required to build (of course)

On Linux, you can set your Java directory using `export JAVA_HOME="/path/to/jdk"` and the Android SDK with `export ANDROID_SDK="/path/to/sdk"` or by using `ANDROID_HOME`

To build, clone the repository and simply run `../gradlew clean assembleRelease (or assembleDebug for a debug release)`

The build instructions/commands are also on README.md, but it's also provided here:
```
# exports needed env vars before building
export JAVA_HOME="/path/to/jdk"
export ANDROID_SDK="/path/to/sdk"

# clone the repository and then build
git clone https://github.com/omaawr/tuisku
cd tuisku/app
../gradlew assembleDebug (or assembleRelease)
# you can install the apk and test with this command, with adb installed and a device connected of course
adb install ./app/build/outputs/apk/release/app-release.apk # replace release with debug if your compiling a debug one
```

To sign your build (release or debug) you can also add this to your build.gradle.kts:
```kotlin
android {
    // add this signingConfigs block
    signingConfigs {
        create("release") {
            storeFile = File("${System.getenv("STORE_FILE")}")
            storePassword = System.getenv("STORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release") // this too to buildTypes
            // the rest of the release config, which includes r8
        }
    }
}
```
then you can set these four enviornment variables before building:
```
export STORE_FILE="/path/to/keystore"
export STORE_PASSWORD="<insert keystore password>"
export KEY_ALIAS="<insert key alias>"
export KEY_PASSWORD="<insert key password>"
```