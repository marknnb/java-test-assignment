CREATE TABLE IF NOT EXISTS client (
    id SERIAL PRIMARY KEY,
    credentials VARCHAR(255),
    active BOOLEAN,
    tariff_plan_id INTEGER
);

CREATE TABLE IF NOT EXISTS tariff_plan (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    price INTEGER,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS backups (
    id SERIAL PRIMARY KEY,
    client_id INTEGER,
    start_date BIGINT,
    end_date BIGINT,
    database_size INTEGER,
    backup_time INTEGER,
    status VARCHAR(255)
);

INSERT INTO tariff_plan (name, price, description) VALUES ('FREE', 0, 'Free plan');
INSERT INTO tariff_plan (name, price, description) VALUES ('STANDARD', 100, 'Standard plan');
INSERT INTO tariff_plan (name, price, description) VALUES ('PREMIUM', 200, 'Premium plan');

INSERT INTO client (credentials, active, tariff_plan_id) 
SELECT '{"username": "user' || generate_series(1, 5) || '", "password": "password' || generate_series(1, 5) || 
'", "host": "host' || generate_series(1, 5) || '", "port": "port' || generate_series(1, 5) || '"}', 
random() < 0.5, (SELECT id FROM tariff_plan ORDER BY random() LIMIT 1) FROM generate_series(1, 50000);


INSERT INTO backups (client_id, start_date, end_date, database_size, backup_time, status)
SELECT generate_series(1, 50),
    (SELECT (EXTRACT(EPOCH FROM (NOW() - '1 day'::INTERVAL * random() * 100))::BIGINT)),
    (SELECT (EXTRACT(EPOCH FROM (NOW() - '1 day'::INTERVAL * random() * 100))::BIGINT)),
    (SELECT (random() * 100000)::INTEGER),
    (SELECT (random() * 100)::INTEGER),
    (SELECT CASE WHEN random() < 0.5 THEN 'success' ELSE 'failed' END)
FROM generate_series(1, 100000);
