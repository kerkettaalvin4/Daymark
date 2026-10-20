# Daymark

Daymark is a small Android to-do app built with Kotlin and Jetpack Compose. You can add a task, include a note, choose its priority, and set a due date. Tasks stay on the device, so the app works without an account.

## What it does

- Add, complete, and delete tasks
- Add an optional note, due date, and low, medium, or high priority
- Filter the list by all tasks, today, upcoming, or completed
- See how many tasks are left and how many are done
- Clear completed tasks when you want a fresh list

Task data is stored locally with Room. The interface is built with Compose and Material 3, and the screen observes task updates through a `Flow`.

## Built with

Kotlin · Jetpack Compose · Material 3 · Room · Coroutines and Flow · AndroidX ViewModel

## Run the app

The project targets Android SDK 36, supports Android 8.0 (API 26) and newer, and uses JDK 17 and Gradle 8.13.

The Gradle wrapper scripts are not included yet. If Gradle 8.13 is installed, run this from the project folder:

```shell
gradle :app:assembleDebug
```

To add the wrapper for future builds, run:

```shell
gradle wrapper --gradle-version 8.13
```

Then open the project in Android Studio, let Gradle sync, and run the `app` configuration on an emulator or connected device.

## Project layout

```text
app/src/main/java/com/example/daymark/
├── data/       Room database, task model, DAO, and repository
├── ui/         Compose screen and theme
├── MainActivity.kt
└── TaskViewModel.kt
```

## License

Daymark is available under the MIT License. See [LICENSE](LICENSE).

warning: in the working copy of 'README.md', LF will be replaced by CRLF the next time Git touches it