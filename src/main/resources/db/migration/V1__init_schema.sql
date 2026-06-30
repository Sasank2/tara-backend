-- ============================================================
-- Tara Platform - Initial Schema
-- ============================================================

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    firebase_uid    VARCHAR(128) NOT NULL UNIQUE,
    email           VARCHAR(255) NOT NULL UNIQUE,
    name            VARCHAR(255) NOT NULL,
    phone           VARCHAR(20),
    profile_image   VARCHAR(500),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_firebase_uid ON users(firebase_uid);
CREATE INDEX idx_users_email ON users(email);

CREATE TABLE birth_profiles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    profile_name    VARCHAR(255) NOT NULL DEFAULT 'My Profile',
    full_name       VARCHAR(255) NOT NULL,
    date_of_birth   DATE NOT NULL,
    time_of_birth   TIME NOT NULL,
    place_of_birth  VARCHAR(500) NOT NULL,
    latitude        DECIMAL(10, 7) NOT NULL,
    longitude       DECIMAL(10, 7) NOT NULL,
    timezone        VARCHAR(100) NOT NULL,
    is_primary      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_birth_profiles_user_id ON birth_profiles(user_id);

CREATE TABLE western_charts (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    birth_profile_id    UUID NOT NULL UNIQUE REFERENCES birth_profiles(id) ON DELETE CASCADE,
    sun_sign            VARCHAR(50),
    moon_sign           VARCHAR(50),
    ascendant           VARCHAR(50),
    planet_positions    JSONB,
    houses              JSONB,
    aspects             JSONB,
    chart_json          JSONB,
    generated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE vedic_charts (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    birth_profile_id    UUID NOT NULL UNIQUE REFERENCES birth_profiles(id) ON DELETE CASCADE,
    lagna               VARCHAR(50),
    rashi               VARCHAR(50),
    nakshatra           VARCHAR(100),
    dasha               JSONB,
    planet_positions    JSONB,
    panchang            JSONB,
    chart_json          JSONB,
    generated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE daily_planets (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    planet_date         DATE NOT NULL UNIQUE,
    moon_position       VARCHAR(100),
    mercury_position    VARCHAR(100),
    venus_position      VARCHAR(100),
    mars_position       VARCHAR(100),
    jupiter_position    VARCHAR(100),
    saturn_position     VARCHAR(100),
    sun_position        VARCHAR(100),
    planet_json         JSONB,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_daily_planets_date ON daily_planets(planet_date);

CREATE TABLE mood_checkins (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    mood            VARCHAR(50) NOT NULL,
    stress_level    VARCHAR(20),
    energy_level    VARCHAR(20),
    sleep_quality   VARCHAR(20),
    note            TEXT,
    checkin_date    DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_mood_checkins_user_id ON mood_checkins(user_id);
CREATE INDEX idx_mood_checkins_date ON mood_checkins(user_id, checkin_date DESC);

CREATE TABLE daily_guidance (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    guidance_date       DATE NOT NULL DEFAULT CURRENT_DATE,
    energy              VARCHAR(50),
    focus_area          VARCHAR(255),
    what_today_means    TEXT,
    focus_guidance      TEXT,
    avoid_guidance      TEXT,
    favorable_time      VARCHAR(100),
    wellness_action     TEXT,
    practical_step      TEXT,
    reflection_prompt   TEXT,
    astrology_snapshot  JSONB,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, guidance_date)
);

CREATE INDEX idx_daily_guidance_user_date ON daily_guidance(user_id, guidance_date DESC);

CREATE TABLE tara_conversations (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category            VARCHAR(50),
    question            TEXT NOT NULL,
    short_answer        TEXT,
    why_tara_says       TEXT,
    practical_step      TEXT,
    wellness_suggestion TEXT,
    reflection_question TEXT,
    is_saved            BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tara_conversations_user_id ON tara_conversations(user_id);
CREATE INDEX idx_tara_conversations_created_at ON tara_conversations(user_id, created_at DESC);

CREATE TABLE journal_entries (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                 UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    entry_date              DATE NOT NULL DEFAULT CURRENT_DATE,
    prompt                  TEXT,
    content                 TEXT NOT NULL,
    mood                    VARCHAR(50),
    stress_level            VARCHAR(20),
    energy_level            VARCHAR(20),
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_journal_entries_user_id ON journal_entries(user_id);
CREATE INDEX idx_journal_entries_date ON journal_entries(user_id, entry_date DESC);

CREATE TABLE user_preferences (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                 UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    focus_areas             JSONB DEFAULT '[]',
    language                VARCHAR(10) NOT NULL DEFAULT 'en',
    notifications_enabled   BOOLEAN NOT NULL DEFAULT TRUE,
    daily_reminder_time     TIME DEFAULT '08:00:00',
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE fcm_tokens (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       VARCHAR(500) NOT NULL UNIQUE,
    device_type VARCHAR(20),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fcm_tokens_user_id ON fcm_tokens(user_id);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN NEW.updated_at = NOW(); RETURN NEW; END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_birth_profiles_updated_at BEFORE UPDATE ON birth_profiles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_user_preferences_updated_at BEFORE UPDATE ON user_preferences FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_journal_entries_updated_at BEFORE UPDATE ON journal_entries FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_fcm_tokens_updated_at BEFORE UPDATE ON fcm_tokens FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
