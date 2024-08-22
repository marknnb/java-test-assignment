CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255),
    email VARCHAR(255),
    role VARCHAR(255)
);

INSERT INTO users (username, password, email, role)
SELECT 'user' || generate_series(1, 1000), 'password', 'user' || generate_series(1, 100) || '@example.com', 'user'
FROM generate_series(1, 1000);
