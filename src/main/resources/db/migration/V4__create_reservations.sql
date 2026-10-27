CREATE TABLE reservations
(
    id           UUID PRIMARY KEY,
    room_id UUID NOT NULL REFERENCES rooms (id),
    guest_id UUID NOT NULL REFERENCES guests (id),
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    guest_count INTEGER NOT NULL check ( guest_count > 0 ),
    status VARCHAR(20) NOT NULL,
    total_price NUMERIC(10, 2) NOT NULL check (total_price > 0),
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT chk_reservations_dates CHECK (check_out > check_in)
);

CREATE INDEX idx_reservations_room_id ON reservations (room_id);
CREATE INDEX idx_reservations_guest_id ON reservations (guest_id);