# Testing checklist

The automated results below are from `gradlew.bat assembleDebug testDebugUnitTest` on 2026-10-21. Unit tests use constructed model objects; they do not prove Android database or UI behavior. Mark manual rows only after observing the running app.

| Test | Expected result | Actual result | Status |
|---|---|---|---|
| Debug APK compile | Build succeeds | Gradle build succeeded | Pass |
| All ingredients sufficient | Recipe included | JUnit assertion passed | Pass |
| One or multiple missing | Recipe excluded | JUnit assertions passed | Pass |
| Insufficient quantity | Recipe excluded | JUnit assertion passed | Pass |
| Exact or excess quantity | Recipe included | JUnit assertions passed | Pass |
| Tomato and tomatoes | Same name | JUnit assertion passed | Pass |
| Egg and eggs | Same name | JUnit assertion passed | Pass |
| 1 kg versus 500 g | Included | JUnit assertion passed | Pass |
| 500 g versus 1 kg | Excluded | JUnit assertion passed | Pass |
| 1 L versus 500 mL | Included | JUnit assertion passed | Pass |
| Grams versus millilitres | Excluded | JUnit assertion passed | Pass |
| Empty pantry | No suggestions | JUnit assertion passed | Pass |
| Edited/deleted ingredient | Suggestions recalculate | JUnit assertion passed on changed in-memory input | Pass for service only |
| Compatible duplicate records | Quantities aggregate | JUnit assertion passed | Pass |
| Repeated recipe requirement | Pantry stock cannot be reused twice | JUnit assertion passed | Pass |
| Invalid zero, negative, text, infinity | Rejected | JUnit assertions passed | Pass |
| Create, read, update, delete on device | Persisted records update | Added bread and butter, edited butter to 10 g, deleted butter; list updated | Pass |
| Close and reopen app | Pantry data persists | Force-stopped and reopened app; 2 items remained | Pass |
| Recipe seeding after reopen | Still exactly 20 | Copied emulator SQLite database and queried 20 recipes, 59 ingredient rows | Pass |
| Empty name | Field feedback; no record | Empty name showed field error on emulator | Pass |
| Missing unit | Field feedback; no record | Not manually exercised | Pending |
| Optional expiry date | Saved and shown | Set bread expiry to 2026-10-21 and saw it in the list | Pass |
| Navigation and back | All destinations reachable | Home, Pantry, Recipes, Settings, form and detail traversed; bottom highlight fix checked | Partial pass; system back not fully audited |
| Invalid recipe ID | Graceful unavailable state | Awaiting runtime verification | Pending |
| Empty RecyclerView | Clear empty state | Awaiting runtime verification | Pending |
| Keyboard and rotation | Form remains usable | Awaiting runtime verification | Pending |
| Expiry preference | Pantry indicator changes | Indicator appeared with setting on and disappeared with setting off | Pass |

## Manual run record

Device or emulator: Pixel 9a emulator  Android version: API 36  Date: 2026-10-21

Record actual failures and fixes here before claiming completion.
