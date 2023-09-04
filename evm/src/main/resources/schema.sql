CREATE TABLE IF NOT EXISTS categories (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT UQ_CATEGORY_NAME UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(254) NOT NULL,
    name VARCHAR(250) NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation VARCHAR(2000) NOT NULL,
    category_id INTEGER NOT NULL REFERENCES categories(id),
    confirmed_requests INTEGER,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id BIGINT NOT NULL REFERENCES users(id),
    lat REAL NOT NULL,
    lon REAL NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit INTEGER NOT NULL,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN NOT NULL,
    state VARCHAR(10) NOT NULL,
    title VARCHAR(120) NOT NULL,
    views BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN NOT NULL,
    title VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS events_compilations (
    event_id BIGINT NOT NULL REFERENCES events(id),
    compilation_id INTEGER NOT NULL REFERENCES compilations(id),
    PRIMARY KEY(event_id, compilation_id)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status VARCHAR(10) NOT NULL,
    event_id BIGINT NOT NULL REFERENCES events(id),
    requester_id BIGINT NOT NULL REFERENCES users(id),
    CONSTRAINT UQ_REQUEST UNIQUE (event_id, requester_id)
);