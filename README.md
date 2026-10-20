# Daymark

Daymark is a calm, offline-first todo app for Android. It is built with Kotlin and Jetpack Compose, with tasks stored locally in Room.

## Features

- Create tasks with an optional note, due date, and priority
- Mark tasks complete and remove tasks
- Filter by all, today, upcoming, or completed
- See a live completion summary
- Keep your list across app restarts with a local Room database
- Material 3 interface with coordinated system bars

## Tech stack

- Kotlin
- Jetpack Compose and Material 3
- MVVM with AndroidX ViewModel and lifecycle-aware state collection
- Room, coroutines, and Flow
- Gradle Kotlin DSL

## Run locally

1. Install JDK 17, Android Studio, and Android SDK Platform 36.
2. Install Gradle 8.13 and run `gradle :app:assembleDebug` from this directory, or generate the wrapper first with `gradle wrapper --gradle-version 8.13`.
3. Open the `Daymark` folder in Android Studio, let Gradle sync, then run the `app` configuration on an emulator or Android device (Android 8.0 / API 26 or newer).

The project uses Android Gradle Plugin 8.13.2. If you generate the wrapper, commit the generated `gradlew`, `gradlew.bat`, and `gradle/wrapper/gradle-wrapper.jar` files so GitHub contributors can build with `./gradlew :app:assembleDebug`.

## Project structure

```text
app/src/main/java/com/example/daymark/
├── data/                 # Room entity, DAO, database, repository
├── ui/                   # Compose screens and theme
├── MainActivity.kt       # Activity entry point
└── TaskViewModel.kt      # UI state and task actions
```

## Notes

Daymark stores task data on-device and does not require an account or network access. The application ID is `dev.daymark.todo`; change it before publishing your own Play Store app.

## License

This project is available under the MIT License. See [LICENSE](LICENSE).


