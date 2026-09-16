<h1 align="center">
    <img height="128" alt="icon" src="https://github.com/user-attachments/assets/e3f02486-a104-4246-8fc9-6dd67adee7b6" />
    <br>
    <span>Tuisku</span>
</h1>

A very shrimple and lightweight app used for storing encrypted notes

### Preview
[Image preview of Tuisku](./assets/preview.png)

### Requirements
- Android 9.0 and higher

### Installation
<a href="https://github.com/omaawr/tuisku/releases">Download the APK at the Releases page</a>, or get it on <a href="https://apt.izzysoft.de/fdroid/index/apk/com.omaawr.tuisku">IzzyOnDroid</a>

### Credits
- https://github.com/X1nto/Mauth, for a tiny piece of code that I couldn't figure out at that time

### Build Instructions
```
# you need to install:
# - the android sdk (obviously, set with ANDROID_SDK on loonix)
# - a jdk like temurin/openjdk (recommended, set with JAVA_HOME on loonix)
# clone the repository and cd into it
$ git clone https://github.com/omaawr/tuisku.git && cd tuisku/app
# compile!! (chmod +x gradlew if not executable)
$ ../gradlew clean assembleDebug # (or assembleRelease for a release build)
# apk should now be at ./build/outputs/apk/debug/app-debug.apk (or ./app/release/app-release.apk if its a release build)
```

### AI disclaimer
AI generated code is not permitted in this repository.

Pull requests that have AI assistance are not really allowed and would be rejected, this is because AI, when it comes to kotlin/android development, can sometimes make low quality code, or deprecated code, depending on the model (e.g Gemini Flash (or sometimes Pro) which often suggests deprecated code or hallucinates and makes nonexistent code, and yes i've already tested)

For people who feel that AI allows them to do something that they would never otherwise do, this is something thats called Imposter Syndrome, You can do things, and it takes time to learn however the learning process, although it can at times be frustrating and it can be very difficult, don't give up.
