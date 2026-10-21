# Video demonstration guide

Record and narrate this yourself in a single 5–7 minute video. Show the public GitHub repository and actual running app. Use legible resolution, ideally 1080p, and export a compressed H.264 MP4.

## 0:00–1:00 GitHub history

Open the public repository and scroll through the actual commit history. Explain what each real milestone added. Do not describe commits that do not exist.

## 1:00–3:30 live functionality

Start with an empty pantry or a known state. `Buttered toast` requires exactly **bread 2 pieces** and **butter 15 grams**. Show that the recipe is unavailable with no ingredients. Add bread 2 pieces, then butter 15 grams. Open Suggested Recipes and show Buttered toast. Edit butter to 10 grams and show the recipe disappears; restore it to 15 grams and show it returns. Delete butter and show it disappears again. Demonstrate an invalid quantity before a valid save. Close and reopen the app to show a saved bread record persists. Visit Settings and toggle expiry indicators; demonstrate a pantry item expiring within three days.

## 3:30–6:15 explain three concepts

- `DatabaseHelper.java`: `onCreate`, `addPantryItem`, `getPantryItems`, `updatePantryItem`, and `deletePantryItem` show persistence and CRUD.
- `PantryAdapter.java`: `onCreateViewHolder` and `onBindViewHolder` connect rows to a RecyclerView.
- `RecipeMatchingService.java`: `canMake` checks every ingredient, compatible unit and quantity.
- Alternatively, `AddEditIngredientActivity.java` uses an Intent `item_id` extra, and `onSaveInstanceState` preserves the selected expiry date.

## 6:15–7:00 database justification

Explain that SQLite is on device, works offline, and represents recipes and required ingredients with a foreign key. Finish before seven minutes.

Before recording, ensure the actual public GitHub history, device run, personal narration, and report align. The final video file must be included inside the submission ZIP.
