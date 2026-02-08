buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Use a stable version of Gradle plugin
        classpath("com.android.tools.build:gradle:8.13.1")
        // Google services
        classpath("com.google.gms:google-services:4.4.0")
        // Kotlin stable version compatible with Room 2.6.1
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
    }
}
