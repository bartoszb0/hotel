CREATE TABLE room_types
(
    id              UUID PRIMARY KEY,
    name            VARCHAR(50)    NOT NULL UNIQUE,
    capacity        INTEGER        NOT NULL CHECK (capacity > 0),
    price_per_night NUMERIC(10, 2) NOT NULL CHECK (price_per_night > 0)
);

CREATE UNIQUE INDEX uq_room_types_name_lower ON room_types (lower(name));