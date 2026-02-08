-- Drop tables if they exist (in reverse order due to foreign key constraints)
DROP TABLE IF EXISTS beer;
DROP TABLE IF EXISTS customer;

-- Publisher Table

CREATE TABLE beer (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    version          INTEGER,
    beer_name        VARCHAR(255),
    beer_style       VARCHAR(50),
    upc              VARCHAR(50),
    quantity_on_hand INTEGER,
    price            INTEGER,
    created_date     TIMESTAMP,
    update_date      TIMESTAMP);

CREATE TABLE customer (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    version          INTEGER,
    name        VARCHAR(255),
    created_date     TIMESTAMP,
    update_date      TIMESTAMP);