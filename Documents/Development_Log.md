# Development log

- 2026-10-21: Created the Android Studio Gradle project and implemented SQLite pantry CRUD, 20 seeded recipes, matching, activities and adapters. The first offline Gradle build could not resolve AndroidX dependencies because they were not cached. Running Gradle with network access resolved them.
- 2026-10-21: The first build completed. Added unit tests for matching, conversions, missing ingredients, aggregation, recalculation and invalid quantities; the next build and test run passed.
- 2026-10-21: The sandbox initially prevented Gradle from writing to the user cache and Git from writing its index. The permitted elevated executions completed those operations.
- 2026-10-21: Emulator runs verified creation, editing, deletion, restart persistence, 20 seeded recipes and strict recipe appearance/disappearance. A reused Activity kept the wrong bottom navigation item highlighted; setting the selected destination in `onResume` fixed it. A card fade caused unreadable transition captures and was removed.

Further runtime findings and genuine fixes should be added here after emulator/device verification.
