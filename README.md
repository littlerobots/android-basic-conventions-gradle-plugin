# Android basic conventions (ABC) plugin

This is an experimental plugin to configure Android projects with common configuration.
It is meant for small projects that are in between needing their own convention plugins vs duplicating Android, Kotlin and JVM
Gradle configuration in various modules.

This has hardly been tested, it probably doesn't work in KMP projects and is not published to any repository. Use at your
own risk!

## Setup
1. Apply this plugin `nl.littlerobots.android-basic-conventions` to your root `build.gradle` file.
2. Configure the extension

```groovy
androidBasicConventions {
    // defaults to null, if not set here, it must be specified in each module
    minSdk = 29
    // defaults to null
    compileSdk = 33
    // defaults to null
    targetSdk = 33
    // optional, defaults to JavaVersion.VERSION_1_8 for Android, JVM and Kotlin
    jvmTarget = JavaVersion.VERSION_11
    // optional compiler args
    kotlinOptions {
        freeCompilerArgs += "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
        freeCompilerArgs += "-opt-in=androidx.compose.animation.ExperimentalAnimationApi"
        freeCompilerArgs += "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi"
        freeCompilerArgs += "-opt-in=androidx.compose.ui.text.ExperimentalTextApi"
    }
}
```
Finally, apply the plugin `nl.littlerobots.android-basic-conventions` to each module and clean up existing values, if any.

## Configured values
Should be pretty obvious, but the main goal is to have a consistent configuration of the JVM targets and shared configuration
for the Android values `minSdk`, `compileSdk` and `targetSdk`.

Additionally, if a version catalog is used, and it contains a version named `compose-compiler`, this version will be used
to configure the `composeOptions.kotlinCompilerExtensionVersion` value.

