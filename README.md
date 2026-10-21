# Smart Pantry Manager

Smart Pantry Manager is a native Java Android app for tracking leftover ingredients and finding meals that can be made entirely from the current pantry. The pantry begins empty. Recipe suggestions are recomputed from persisted pantry records whenever the suggestions screen resumes.

## Features

- Create, view, edit and delete pantry ingredients with quantity, unit and optional expiry date.
- Browse only recipes for which every required ingredient and quantity is available.
- View full ingredients and numbered preparation steps for each recipe.
- See live dashboard counts and optional expiring soon indicators.
- See four color-coded Home jars for pantry items, possible recipes, expiring-soon items, and expired items.
- Mark a recipe as cooked and reopen it from the five most recently cooked recipes on Home.
- Store pantry and recipes offline with SQLite; persist the alert setting with SharedPreferences.

## Technology and database choice

Java, XML layouts, AndroidX RecyclerView, Material Components, Gradle, and SQLiteOpenHelper. SQLite keeps the data on the device without a network connection, provides relational recipe and ingredient tables, and supports full CRUD with simple SQL that can be explained in the assignment video.

Schema version 2 adds a `recipe_uses` table for recent cooking history. The migration creates that table without deleting Version 1 pantry records.

## Build and run

1. Open `SmartPantry/` as a project in Android Studio.
2. Install Android SDK Platform 35 and a compatible JDK 17. Android Studio's bundled JDK works.
3. Allow Gradle to sync and download dependencies.
4. Run the `app` configuration on an Android emulator or device running Android 8.0/API 26 or later.

From a terminal in `SmartPantry/`, run `gradlew.bat assembleDebug testDebugUnitTest` on Windows or `./gradlew assembleDebug testDebugUnitTest` on macOS/Linux. The APK is generated under `app/build/outputs/apk/debug/`.

## Project layout

- `SmartPantry/app/src/main/java/com/smartpantry/manager/activities/`: six screens.
- `database/DatabaseHelper.java`: schema, CRUD queries and the 20 recipe seeds.
- `services/`: name normalization and strict matching.
- `models/`: pantry and recipe data classes.
- `adapters/`: RecyclerView presentation.
- `utils/`: navigation, validation and unit conversion.
- `SmartPantry/app/src/test/`: matching and validation unit tests.
- `Documents/`: database design, testing checklist, development log, video guide, compliance audit.
- `Report/`: editable report draft with genuine emulator screenshots and student details.
- `Screenshots/`: genuine emulator captures of the implemented screens and flows.

## Strict matching

`RecipeMatchingService.canMake()` checks every required ingredient. It normalizes common variations such as `tomatoes`/`tomato`, combines repeated recipe requirements, aggregates pantry rows only when units share a measurement dimension, converts kilograms to grams and litres to millilitres, and rejects the recipe when any requirement is missing or short. Mass and volume are never equated. There is no partial match list or shopping suggestion.

## Testing

Run `testDebugUnitTest` for matching and validation cases. Follow `Documents/Testing_Checklist.md` on a device for database persistence, navigation, UI and expiry behavior. The checklist distinguishes automated results from manual checks.

## Screenshots and GitHub

The screenshots in `Screenshots/` were captured from the running emulator. The project repository is [FlipFlopXI/smart_pantry_manager](https://github.com/FlipFlopXI/smart_pantry_manager) on the `main` branch. The report cover still needs this working link before submission. Continue recording genuine, incremental development as it happens; the assignment requires at least 10 meaningful commits over the development period.

## Submission

The final ZIP also needs Shiven's genuine 5–7 minute narrated video and the report's personal reflection, source references and working GitHub link. Do not package the final ZIP until those are available. Exclude generated `build/`, `.gradle/`, caches and `local.properties`.
