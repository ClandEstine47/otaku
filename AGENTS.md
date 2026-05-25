# AGENTS Guide

## Project Snapshot
- Android multi-module AniList client (`README.md`) using Kotlin + Compose + Hilt + Apollo GraphQL.
- Modules are intentionally layered (`settings.gradle.kts`): `:app` -> `:feature` -> `:core:data` -> `:core:network`, with `:core:domain` and `:core:navigation` shared.
- Domain contracts live in `core/domain` (interfaces + models), implementations in data/network modules.

## Architecture That Matters
- App entry is `app/src/main/java/com/example/otaku/MainActivity.kt`; it reads login state (`isLoggedIn`) and hosts `OtakuMain`.
- Navigation is type-safe: routes are `@Serializable` types in `core/navigation/OtakuScreen.kt`, used with `composable<OtakuScreen.*>` in `app/src/main/java/com/example/otaku/MainNavigation.kt`.
- Navigation actions are centralized in `core/navigation/NavActionManager.kt`; UI calls manager methods instead of direct route strings.
- Cross-module data flow for content screens: ViewModel (feature) -> `MediaRepository` (domain) -> `MediaRepositoryImpl` (data) -> `MediaService` (domain) -> `MediaServiceImpl` (network) -> Apollo queries.
- GraphQL responses are mapped via extension mappers in `core/network/service/MediaMappers.kt` (`toDomain*` pattern).

## Auth + Deep Link Flow
- OAuth redirect scheme is `com.example.otaku` in `app/src/main/AndroidManifest.xml` and `feature/src/main/java/com/example/feature/Constants.kt`.
- `MainViewModel.onIntentDataReceived` forwards redirect URIs to `MainRepository.parseRedirectUri`.
- Token persistence is in `core/data/repository/MainRepositoryImpl.kt` using `DataStore<Preferences>`.

## Build/Test Workflows
- Use Gradle wrapper from repo root.
- Verified unit test tasks: `.\gradlew.bat :core:data:testDebugUnitTest :core:network:testDebugUnitTest`.
- Common local checks: `.\gradlew.bat spotlessCheck lintDebug testDebugUnitTest assembleDebug`.
- Spotless is enforced globally (`build.gradle.kts`); Kotlin compile tasks depend on `spotlessApply`, so formatting runs before compile.
- Apollo sources are generated from `core/network/src/main/graphql/*.graphql` via `core/network/build.gradle.kts` (`apollo { service("service") ... }`).

## Conventions Specific To This Repo
- Return `Result<T>` from repository/service boundaries instead of throwing; check `MediaServiceImpl` and `MediaRepositoryImpl`.
- Prefer `Optional.presentIfNotNull(...)` when building GraphQL query params (`MediaServiceImpl`).
- Preserve module boundaries: UI/Compose code stays in `:feature`/`:app`; API schemas + network mapping stay in `:core:network`; shared models/interfaces stay in `:core:domain`.
- Typed nav args for parcelables use `CustomNavType` (`core/navigation/CustomNavType.kt`) with serializers from domain models.
- Logging uses Timber (`OtakuApp.kt`, repository implementations).

## When Adding Features
- Add/adjust GraphQL documents under `core/network/src/main/graphql`, then map network models in `MediaMappers.kt`.
- Expose new operations through `MediaService` and `MediaRepository` interfaces before wiring ViewModels.
- Register DI bindings in module-level Hilt modules (`core/data/di/DataModule.kt`, `feature/di/FeatureModule.kt`, `core/network/di/NetworkModule.kt`).
- If navigation is needed, add a new `OtakuScreen` route and a corresponding helper in `NavActionManager`.


