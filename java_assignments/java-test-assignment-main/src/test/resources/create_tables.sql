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


INSERT INTO public.client (id, credentials, active, tariff_plan_id) VALUES (100, e'{
  "username" : "postgres123",
  "password" : "postgres123",
  "database" : "dsoifhasuo9fas70a9fu",
  "host" : "customer_db",
  "port" : "5432"
}', true, 1);

INSERT INTO public.backups (id, client_id, start_date, end_date, database_size, backup_time, status) VALUES (100, 100, 1714488844713, 1714489343787, 4951594, 499074, 'DONE');

INSERT INTO public.backups (id, client_id, start_date, end_date, database_size, backup_time, status) VALUES (101, 100, 1714488844713, 1714489343787, 4951594, 499074, 'FAILED');
