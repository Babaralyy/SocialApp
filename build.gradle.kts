buildscript {
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
        classpath ("com.google.gms:google-services:4.4.0")
        classpath ("com.google.firebase:firebase-crashlytics-gradle:3.0.1")
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.1" apply false
    id ("com.android.library") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
//    id("com.google.gms.google-services") version "4.4.1" apply false
//    id("com.google.firebase.crashlytics") version "3.0.0" apply false
}