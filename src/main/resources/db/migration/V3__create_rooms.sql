CREATE TABLE rooms
(
    id           UUID PRIMARY KEY,
    number       INTEGER NOT NULL UNIQUE CHECK (number > 0),
    room_type_id UUID NOT NULL REFERENCES room_types (id)
);

CREATE INDEX idx_rooms_room_type_id ON rooms (room_type_id);