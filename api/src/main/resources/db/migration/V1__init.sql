CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE stories (
    id UUID PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    slug VARCHAR(80) NOT NULL UNIQUE,
    title VARCHAR(160) NOT NULL,
    author VARCHAR(160) NOT NULL,
    synopsis TEXT NOT NULL,
    price_cents INTEGER NOT NULL CHECK (price_cents >= 0),
    html_file VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE keychains (
    id UUID PRIMARY KEY,
    public_code VARCHAR(64) NOT NULL UNIQUE,
    pin_hash VARCHAR(100),
    story_id UUID NOT NULL REFERENCES stories (id),
    owner_id UUID REFERENCES users (id),
    status VARCHAR(20) NOT NULL,
    pin_attempts INTEGER NOT NULL DEFAULT 0,
    activated_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT keychains_status_check CHECK (status IN ('NEW', 'ACTIVATED')),
    CONSTRAINT keychains_owner_consistency CHECK (
        (status = 'NEW' AND owner_id IS NULL AND activated_at IS NULL AND pin_hash IS NOT NULL)
        OR
        (status = 'ACTIVATED' AND owner_id IS NOT NULL AND activated_at IS NOT NULL AND pin_hash IS NULL)
    )
);

CREATE TABLE entitlements (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id),
    story_id UUID NOT NULL REFERENCES stories (id),
    source VARCHAR(20) NOT NULL,
    keychain_id UUID REFERENCES keychains (id),
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT entitlements_unique_user_story UNIQUE (user_id, story_id),
    CONSTRAINT entitlements_source_check CHECK (source IN ('KEYCHAIN', 'PURCHASE'))
);

CREATE TABLE orders (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id),
    story_id UUID NOT NULL REFERENCES stories (id),
    amount_cents INTEGER NOT NULL CHECK (amount_cents > 0),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    paid_at TIMESTAMPTZ,
    CONSTRAINT orders_status_check CHECK (status IN ('PENDING', 'PAID', 'CANCELLED'))
);

CREATE UNIQUE INDEX orders_one_pending_per_story
    ON orders (user_id, story_id)
    WHERE status = 'PENDING';

CREATE TABLE read_tickets (
    token VARCHAR(80) PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id),
    story_id UUID NOT NULL REFERENCES stories (id),
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_entitlements_user ON entitlements (user_id);
CREATE INDEX idx_orders_user ON orders (user_id);
CREATE INDEX idx_keychains_owner ON keychains (owner_id);
CREATE INDEX idx_read_tickets_expires ON read_tickets (expires_at);
