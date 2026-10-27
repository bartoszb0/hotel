CREATE TABLE guests
(
    id         UUID PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL
);

CREATE UNIQUE INDEX uq_guests_email_lower ON guests (lower(email));