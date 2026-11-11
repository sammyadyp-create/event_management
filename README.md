# CampusEvents

A simple college event management Android app prototype built with Kotlin, Jetpack Compose and Material 3.

## Open in Android Studio

1. Extract `CampusEvents.zip`.
2. Open the extracted `CampusEvents` folder in Android Studio.
3. Install Android SDK Platform 36 if Android Studio asks for it.
4. Set Gradle JDK to JDK 17 in Settings > Build, Execution, Deployment > Build Tools > Gradle.
5. Sync the project and run the `app` configuration on an emulator or Android device.

## Current prototype

- Student home, explore/search, event details, My Events and profile screens.
- Admin event list with an in-memory add and delete demo.
- Sample event data.

## Notes

- This is currently a UI prototype. Login, real event registration, persistence, Firebase and admin/student role authentication are not connected yet.
- `local.properties` is intentionally excluded because its Android SDK path is specific to one computer.
- The Gradle wrapper scripts and wrapper JAR are not included. Android Studio can open the project and configure Gradle; if wrapper files are needed, use the Gradle wrapper task from an installed Gradle 8.13 distribution.
