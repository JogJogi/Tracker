# FitTracker — Developer Guide

Offline-first Android fitness tracker. FLOSS, F-Droid-compatible, no Google Play Services.

## Architecture

**MVVM + UDF** — single module, pragmatic structure. No MVI framework.

```
com.example.fittracker/
  data/         # Room, Repositories, Sensor wrappers, Exporters
  di/           # Koin modules
  domain/       # Shared models (minimal)
  ui/           # Compose screens, ViewModels, Navigation, Widgets, Theme
  service/      # Foreground Services, BroadcastReceivers
  util/         # Pure functions (distance, format, conversions)
```

## Key Decisions

- **DI**: Koin 4.x (not Hilt) — flat learning curve for solo dev
- **Location**: `android.location.LocationManager` only — no GMS/FusedLocation (F-Droid requirement)
- **Maps**: MapLibre Native 11.x with PMTiles (offline vector tiles)
- **Barcode**: ZXing (not ML Kit — proprietary)
- **BLE HR**: Nordic BLE Library 2.x (Apache 2.0)
- **DB**: Room 2.8.x as primary store; Health Connect as optional bridge only
- **Widgets**: Jetpack Glance 1.1.x
- **Steps**: `Sensor.TYPE_STEP_COUNTER` (hardware, battery-efficient)

## Build Variants

- `foss` flavor: pure FOSS, F-Droid compatible
- `gplay` flavor: may add GMS enhancements

## Conventions

- StateFlow<UiState> in ViewModels, SharedFlow for one-shot events
- Room queries return `Flow<T>` for live updates
- Use `@AutoMigration` + schema JSON export for Room migrations
- ISO-8601 timestamps everywhere (`kotlinx-datetime` `Instant`)
- Error handling: `Result<T>` sealed class at repository boundaries
- No comments on obvious code; comment only non-obvious invariants

## Testing

Target 30-40% coverage, business logic only:
- Distance/pace/calorie calculations (`util/`)
- GPS filter logic
- Repository tests with Room in-memory DB + Fakes

Room migration tests are **mandatory** before any schema change.

## F-Droid Checklist

- [ ] No proprietary SDK (GMS, Firebase, ML Kit)
- [ ] `bundle { enabled = false }` — F-Droid needs APK not AAB
- [ ] NDK version pinned in `local.properties`
- [ ] `./gradlew assembleFossRelease` for reproducible builds
- [ ] apksigcopier roundtrip test before submission

## Permissions Required

| Permission | Reason |
|---|---|
| `ACTIVITY_RECOGNITION` | Step counter (API 29+) |
| `ACCESS_FINE_LOCATION` | GPS workout tracking |
| `FOREGROUND_SERVICE_LOCATION` | GPS FGS (Android 14+) |
| `FOREGROUND_SERVICE_HEALTH` | Sensor FGS (Android 14+) |
| `BLUETOOTH_SCAN` / `BLUETOOTH_CONNECT` | BLE HR monitors |
| `RECEIVE_BOOT_COMPLETED` | Reset step counter offset on reboot |

## Running Locally

```bash
./gradlew assembleFossDebug
./gradlew testFossDebugUnitTest
./gradlew connectedFossDebugAndroidTest
```
