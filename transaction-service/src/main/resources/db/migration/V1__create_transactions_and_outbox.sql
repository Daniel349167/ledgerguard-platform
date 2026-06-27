CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(120) NOT NULL UNIQUE,
    account_debit VARCHAR(80) NOT NULL,
    account_credit VARCHAR(80) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL,
    country VARCHAR(2) NOT NULL,
    new_beneficiary BOOLEAN NOT NULL,
    status VARCHAR(24) NOT NULL,
    risk_score INTEGER,
    decision_reasons TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    published_at TIMESTAMPTZ
);

CREATE INDEX idx_outbox_unpublished
    ON outbox_events (created_at)
    WHERE published_at IS NULL;
