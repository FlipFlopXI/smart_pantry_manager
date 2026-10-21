# Assignment compliance matrix

| Requirement | Implementation location | Verification method | Result | Remaining issue |
|---|---|---|---|---|
| Java Android app and five required screens | `SmartPantry/app/src/main/java/.../activities/` | Gradle compile; emulator navigation | Pass for tested screens | System back and rotation still pending |
| Pantry CRUD and persistent storage | `DatabaseHelper.java`, Pantry and Add/Edit activities | Emulator create/edit/delete/reopen | Pass | None known |
| Dynamic database list with custom adapter | `PantryAdapter.java` | Emulator list check | Pass | None known |
| 20 seeded recipes | `DatabaseHelper.seedRecipes()` | Query emulator SQLite | 20 recipe rows, 59 ingredient rows | None known |
| Strict full ingredient matching | `RecipeMatchingService.java` | JUnit edge cases and emulator butter flow | Pass | None known |
| Robust names and unit conversions | `IngredientNormalizer.java`, `UnitConverter.java` | JUnit edge cases | Pass | None known |
| Recipe detail | `RecipeDetailActivity.java` | Emulator open and invalid ID | Valid detail pass | Invalid ID runtime pending |
| Settings with actual alert behavior | `SettingsActivity.java`, `PantryAdapter.java` | Toggle and inspect expiring item | Pass | None known |
| Bottom navigation and Intents | `NavigationHelper.java`, activities | Emulator traversal and highlight check | Pass for tested paths | System back audit pending |
| Input validation | `AddEditIngredientActivity.java`, `ValidationUtils.java` | JUnit and emulator blank name | Partial pass | Missing unit and quantity UI tests pending |
| No maps or location | Manifest and code | Source review | Pass | None |
| Public GitHub with minimum 10 meaningful commits | `https://github.com/FlipFlopXI/smart_pantry_manager` | Inspect public remote history | Repository configured and pushed; 10-commit requirement pending | Continue genuine development commits and verify public access |
| 5–7 minute narrated video | `Documents/Video_Demonstration_Guide.md` | Review actual video | Not supplied | Student records video |
| Report with genuine screenshots, details and link | `Report/` | Review completed report | Draft with screenshots and student details | Personal reflection, source list and live GitHub URL pending |
| One ZIP under 50 MB | Final submission packaging | Inspect archive | Not created | Wait for real video and report |
