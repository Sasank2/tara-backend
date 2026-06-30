# Tara Backend

> AI-powered Astral/Vedic Life Guide — Spring Boot REST API

---

## What is Tara?

Tara is not a horoscope app. It is an AI-powered Astral/Vedic Life Guide that helps users understand their daily energy, emotional state, relationships, career, wellness, and life direction — using Western astrology (Astral) and Indian astrology (Vedic) as a lens.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2.5 |
| Database | PostgreSQL 15 |
| Auth | Firebase Authentication |
| AI | Anthropic Claude (Haiku) |
| Astrology API | AstrologyAPI.com |
| Geocoding | GeoNames |
| ORM | Spring Data JPA / Hibernate |
| Migration | Flyway |
| Security | Spring Security + Firebase JWT filter |
| Build | Maven |

---

## Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 15+
- A Firebase project with a service account JSON
- API keys for AstrologyAPI and GeoNames
- An Anthropic API key

---

## Local Setup

### 1. Clone the repo

```bash
git clone <repo-url>
cd tara
```

### 2. Create the PostgreSQL database

```sql
CREATE USER tara_user WITH PASSWORD 'tara_pass';
CREATE DATABASE tara_db OWNER tara_user;
GRANT ALL PRIVILEGES ON DATABASE tara_db TO tara_user;
```

### 3. Add Firebase service account

Download your Firebase service account JSON from the Firebase Console and place it at:

```
src/main/resources/firebase-service-account.json
```

### 4. Configure environment variables (or use defaults)

The app uses sensible defaults for local development. To override, set these environment variables:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=tara_db
export DB_USERNAME=tara_user
export DB_PASSWORD=tara_pass

export FIREBASE_PROJECT_ID=your-firebase-project-id
export FIREBASE_CREDENTIALS_PATH=firebase-service-account.json

export AI_API_KEY=sk-ant-...
export AI_MODEL=claude-haiku-4-5-20251001

export ASTROLOGY_API_USER_ID=your-user-id
export ASTROLOGY_API_KEY=your-api-key

export GEONAMES_USERNAME=your-geonames-username
```

### 5. Run the backend

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/api`.

---

## Project Structure

```
src/main/java/com/tara/
├── auth/                   # Firebase auth filter + principal
├── common/                 # Shared exceptions, API response wrapper
├── config/                 # Security config, Firebase config, WebClient
├── home/                   # Home screen data aggregation
├── journal/                # Journal entries
├── mood/                   # Mood check-ins
├── notification/           # FCM push notifications
├── planets/                # Daily planetary positions (scheduled)
├── profile/                # Birth profile — create, get, update
├── taraAi/                 # AI chat — Claude integration + prompt builder
├── user/                   # User registration and profile
├── vedic/                  # Vedic chart generation (AstrologyAPI)
└── western/                # Western chart generation (AstrologyAPI)
```

---

## API Reference

All endpoints are prefixed with `/api`. All endpoints except health require a valid Firebase ID token in the `Authorization: Bearer <token>` header.

### Auth

Authentication is handled via Firebase. The app calls Firebase's REST API to sign in or sign up, receives an `idToken`, and passes it to all backend requests. The backend's `FirebaseAuthFilter` verifies the token on every request and auto-registers new users on their first authenticated call.

---

### User

#### GET /api/user
Returns the current user's profile. Auto-registers on first call.

**Response**
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "name": "Tara User",
    "email": "user@example.com",
    "hasBirthProfile": true,
    "hasWesternChart": true,
    "hasVedicChart": true,
    "createdAt": "2026-06-05T22:53:49Z"
  }
}
```

#### PATCH /api/user
Updates user profile fields (name, phone, profileImage).

---

### Birth Profile

#### POST /api/profile/birth
Creates a birth profile. Resolves location via GeoNames. Generates Western and Vedic charts automatically.

**Request**
```json
{
  "fullName": "Sasank Reddy Mukku",
  "dateOfBirth": "1998-11-02",
  "timeOfBirth": "13:30",
  "placeOfBirth": "Kanigiri, India"
}
```

**Response** — returns the created profile with resolved lat/long/timezone.

#### GET /api/profile/birth
Returns the user's primary birth profile.

**Response**
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "fullName": "Sasank Reddy Mukku",
    "dateOfBirth": "1998-11-02",
    "timeOfBirth": "13:30",
    "placeOfBirth": "Kanigiri, India",
    "latitude": 15.40555,
    "longitude": 79.50694,
    "timezone": "Asia/Kolkata",
    "primary": true
  }
}
```

#### PUT /api/profile/birth
Updates the primary birth profile. Re-resolves location if place changed. Regenerates both Western and Vedic charts. Existing charts are safely deleted before regeneration.

**Request** — same shape as POST.

---

### Charts

#### GET /api/charts/western
Returns the user's Western astrology chart (Sun, Moon, Rising, houses, aspects).

#### GET /api/charts/vedic
Returns the user's Vedic astrology chart (Rashi, Nakshatra, Lagna, planet positions).

---

### Planets

#### GET /api/planets/today
Returns today's planetary positions. Fetched from AstrologyAPI and cached daily. A scheduled job runs at 1am UTC every day to refresh positions automatically.

---

### Home

#### GET /api/home
Returns the aggregated home screen payload including greeting, today's guidance, astrology header, and mood context. Caches guidance per user per day.

**Response includes:**
- `greeting` — personalized greeting with user's name
- `astrologyHeader` — Sun sign, Moon sign, Ascendant
- `todaysGuidance` — focus area, what today means, focus guidance, practical step, wellness action, avoid guidance
- `latestMood` — user's most recent mood check-in

---

### Mood

#### POST /api/mood
Saves a mood check-in.

**Request**
```json
{
  "mood": "Calm",
  "stressLevel": "Low",
  "energyLevel": "High",
  "sleepQuality": "Good"
}
```

#### GET /api/mood/latest
Returns the user's most recent mood check-in.

---

### Tara AI

#### POST /api/taraAi/chat
Sends a question to Tara's AI and returns a structured response. Uses the user's birth chart, current planetary positions, and latest mood to personalise the answer.

> **Important:** the request field must be `question`, not `message`.

**Request**
```json
{
  "question": "Why do I feel so restless today?",
  "category": "emotional"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "question": "Why do I feel so restless today?",
    "shortAnswer": "...",
    "whyTaraSays": "...",
    "practicalStep": "...",
    "wellnessSuggestion": "...",
    "reflectionQuestion": "...",
    "disclaimer": "This guidance is for personal reflection only.",
    "saved": false
  }
}
```

#### GET /api/taraAi/history?limit=10
Returns the user's recent AI conversation history.

#### GET /api/taraAi/saved
Returns conversations the user has saved.

#### POST /api/taraAi/{id}/save
Toggles the saved state of a conversation.

---

### Journal

#### GET /api/journal
Returns all journal entries for the current user, ordered by most recent.

#### POST /api/journal
Creates a new journal entry.

**Request**
```json
{
  "content": "Today I felt...",
  "mood": "Reflective"
}
```

---

## Database Schema (key tables)

| Table | Purpose |
|---|---|
| `users` | Firebase UID, email, name |
| `birth_profiles` | Full birth details, lat/long, timezone, is_primary flag |
| `western_charts` | Sun/Moon/Rising, houses, aspects JSON (one per birth profile) |
| `vedic_charts` | Rashi/Nakshatra/Lagna, planet positions JSON (one per birth profile) |
| `daily_planets` | Today's planetary positions, refreshed daily at 1am UTC |
| `daily_guidance` | AI-generated daily guidance, cached per user per day |
| `mood_checkins` | User mood logs with stress/energy/sleep fields |
| `journal_entries` | Free-text journal entries with optional mood tag |
| `tara_conversations` | AI chat history with full structured response fields |
| `user_preferences` | Focus areas, language, notification settings |
| `fcm_tokens` | Firebase Cloud Messaging tokens for push notifications |

---

## Key Design Decisions

**Firebase-only auth** — There is no username/password stored in the backend. All auth is delegated to Firebase. The backend only stores the `firebaseUid` and uses it to look up the local user row. New users are auto-registered on their first authenticated API call via `UserService.registerOrGet()`.

**Idempotent chart generation** — `WesternChartService.generateChart()` and `VedicChartService.generateChart()` both delete any existing chart row for the birth profile before inserting a new one, with an explicit `flush()` call to prevent Hibernate batching from causing a unique constraint violation on the `birth_profile_id` foreign key.

**Prompt-based AI responses** — `TaraPromptBuilder` constructs a structured system prompt and user prompt that includes the user's birth chart, planetary positions, and mood context. Claude is instructed to respond in a specific JSON schema (`shortAnswer`, `whyTaraSays`, `practicalStep`, `wellnessSuggestion`, `reflectionQuestion`). If the response is not valid JSON, `TaraAiService.parseAndSave()` catches the exception and falls back to storing the raw text as `shortAnswer`.

**Location resolution** — When a birth profile is created or updated, if the place of birth changes, the backend calls GeoNames to resolve the latitude, longitude, and timezone. These values are then passed to AstrologyAPI for accurate chart generation.

---

## Running in Production

The app is designed to run free locally or on Oracle Cloud Free Tier. For production:

1. Set all environment variables via your hosting platform's secrets manager.
2. Change `spring.jpa.hibernate.ddl-auto` from `validate` to `none` and use Flyway migrations only.
3. Point `FIREBASE_CREDENTIALS_PATH` to a mounted secret file.
4. Set `FCM_ENABLED=true` and provide FCM credentials for push notifications.

---

## Known Issues

- `user_preferences` table exists but the mobile app currently saves intentions/feeling/frequency to `AsyncStorage` only — backend sync is not yet implemented.
- Daily guidance is cached per user per day but does not yet invalidate when the user updates their birth profile mid-day.

---

## License

Private — Tara is a proprietary product. All rights reserved.
