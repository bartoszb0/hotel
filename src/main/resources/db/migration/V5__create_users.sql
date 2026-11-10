CREATE TABLE users
(
    id            UUID PRIMARY KEY,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(60)  NOT NULL,
    role          VARCHAR(20)  NOT NULL CHECK (role IN ('GUEST', 'STAFF')),
    guest_id      UUID UNIQUE REFERENCES guests (id)
);

CREATE UNIQUE INDEX uq_users_email_lower ON users (lower(email));