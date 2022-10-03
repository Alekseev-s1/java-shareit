DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users
(
    user_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR
(
    255
) NOT NULL,
    email VARCHAR
(
    512
) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS requests
(
    request_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    description
    TEXT
    NOT
    NULL,
    requestor_id
    BIGINT
    REFERENCES
    users
(
    user_id
) ON DELETE CASCADE,
    created TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS items
(
    item_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR
(
    255
) NOT NULL,
    description TEXT NOT NULL,
    available BOOLEAN NOT NULL,
    owner_id BIGINT REFERENCES users
(
    user_id
) ON DELETE CASCADE,
    request_id BIGINT REFERENCES requests
(
    request_id
)
  ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    item_id
    BIGINT
    REFERENCES
    items
(
    item_id
) ON DELETE SET NULL,
    booker_id BIGINT REFERENCES users
(
    user_id
)
  ON DELETE SET NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status varchar
(
    30
) NOT NULL
    );

CREATE TABLE IF NOT EXISTS comments
(
    comment_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    text
    TEXT
    NOT
    NULL,
    author_id
    BIGINT
    REFERENCES
    users
(
    user_id
) ON DELETE CASCADE NOT NULL,
    item_id BIGINT REFERENCES items
(
    item_id
)
  ON DELETE CASCADE NOT NULL,
    created_at TIMESTAMP
  WITHOUT TIME ZONE NOT NULL
    );