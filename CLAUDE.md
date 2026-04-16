# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Han Culture Social App — an Android client for a Han culture community platform. Built with **Java** and **traditional View + XML** UI system (no Compose). Communicates with a Spring Boot backend via REST API.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean and build
./gradlew clean assembleDebug

# Run on connected device/emulator
./gradlew installDebug

# Run on specific emulator
./gradlew installDebug -Pandroid.testInstrumentationRunnerArguments.class=com.example.han.ExampleUnitTest
```

## Architecture

- **Language**: Java 11
- **Min SDK**: 24 (Android 7.0), **Target SDK**: 34
- **UI System**: View + XML layouts (no Compose)
- **Build**: Gradle with Kotlin DSL (`build.gradle.kts`), version catalog (`libs.versions.toml`)

### Layered Structure

```
Activity/Fragment → Retrofit/OkHttp → Backend REST API
    ↕ XML Layouts       ↕ Gson JSON       ↕ JWT Auth
    ↕ RecyclerView      ↕ Glide Images    ↕ Token Refresh
    ↕ Adapter           ↕ OkHttp Client
```

| Layer | Package | Purpose |
|-------|---------|---------|
| Activity | `com.example.han.activity` | Screen containers: Login, Main, PostDetail, EditProfile, CreatePost |
| Fragment | `com.example.han.fragment` | Reusable UI: Home (post list), Profile, MyPosts, Login, Register |
| Adapter | `com.example.han.adapter` | RecyclerView adapters + ViewPager2 adapter |
| Network | `com.example.han.network` | Retrofit client, OkHttp, auth interceptor, token authenticator |
| Model | `com.example.han.model` | DTOs for API request/response, entity classes |
| Util | `com.example.han.util` | Constants, Toast helper, time formatter |

## Dependencies

- **Retrofit 2.9.0** — HTTP client
- **OkHttp 4.12.0** — with `HttpLoggingInterceptor` and custom `Authenticator`
- **Gson 2.10.1** — JSON serialization (via `retrofit-converter-gson`)
- **Glide 4.16.0** — image loading and caching
- **Material 1.11.0** — Material Design 3 components
- **RecyclerView** — list display
- **SwipeRefreshLayout** — pull-to-refresh
- **ViewPager2** — login/register tab switching
- **CardView** — card containers
- **CircleImageView 3.1.0** — avatar display
- **Security Crypto** — encrypted SharedPreferences for token storage

## Network Configuration

- **Base URL**: `http://10.0.2.2:8080/` (emulator access to host machine)
  - For **real device** on same network: use host machine's LAN IP (e.g. `192.168.x.x:8080`)
  - For **production**: change to `https://api.hanculture.com/`
- **Cleartext**: enabled (`usesCleartextTraffic="true"`) — disable for production HTTPS

### Token Authentication

1. `AuthInterceptor` adds `Authorization: Bearer <token>` to every request
2. On 401 response, `AuthAuthenticator` auto-refreshes using refresh token
3. `AtomicBoolean` prevents concurrent token refresh
4. Token stored in `EncryptedSharedPreferences` for security

### API Endpoints

All paths under `/api`:

- **User**: `POST /register`, `POST /login`, `POST /refresh`, `GET /profile`, `PUT /profile`, `POST /logout`
- **Post**: `GET /posts` (list with pagination), `GET /posts/{id}` (detail + comments), `POST /posts` (create), `DELETE /posts/{id}`, `GET /posts/my`
- **Comment**: `POST /comments`, `DELETE /comments/{id}`
- **Like**: `POST /likes/{postId}`, `DELETE /likes/{postId}`, `GET /likes/check/{postId}`
- **Upload**: `POST /upload/image` (multipart, to Aliyun OSS)

## Key Implementation Notes

- **Request DTOs** must have **getters** — Gson serializes via getter methods, not fields
- **Pagination**: page starts from 0, default size=10, max size=50
- **Post types**: `Hanfu`, `Poetry`, `Music`, `Etiquette`, `Solar`, `UserPost`
- **Image upload**: uses `ActivityResultContracts.GetContent` for system image picker, converts to `MultipartBody.Part` for upload
- **Post list**: SwipeRefreshLayout for pull-to-refresh, scroll-based auto load-more
- **Token refresh flow**: 401 → check AtomicBoolean → call `/api/user/refresh` → save new tokens → retry original request
- **Profile updates**: avatar upload returns URL first, then URL is saved with bio in a single profile update call
- **Bottom navigation**: Home (post list), Publish (create post), Profile (user info + my posts)

## File Locations

- Source: `app/src/main/java/com/example/han/`
- Layouts: `app/src/main/res/layout/`
- Resources: `app/src/main/res/values/`
- Manifest: `app/src/main/AndroidManifest.xml`
- Dependencies: `app/build.gradle.kts`, `gradle/libs.versions.toml`

## Backend

The corresponding backend project is at `../Han-Back` — Spring Boot 3.2 with MyBatis, Redis, and JWT authentication. See `Han-Back/CLAUDE.md` for backend details.
