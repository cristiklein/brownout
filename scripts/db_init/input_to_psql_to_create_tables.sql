--CREATE DATABASE rubis;
--connect rubis;

CREATE TABLE categories (
   id   SERIAL,
   name VARCHAR(50),
   PRIMARY KEY(id)
);

CREATE TABLE regions (
   id   SERIAL,
   name VARCHAR(25),
   PRIMARY KEY(id)
);

CREATE TABLE users (
   id            SERIAL,
   firstname     VARCHAR(20),
   lastname      VARCHAR(20),
   nickname      VARCHAR(20) NOT NULL UNIQUE,
   password      VARCHAR(20) NOT NULL,
   email         VARCHAR(50) NOT NULL,
   rating        INTEGER,
   balance       FLOAT,
   creation_date TIMESTAMP,
   region        INTEGER NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE items (
   id            SERIAL,
   name          VARCHAR(100),
   description   TEXT,
   initial_price FLOAT NOT NULL,
   quantity      INTEGER NOT NULL,
   reserve_price FLOAT DEFAULT 0,
   buy_now       FLOAT DEFAULT 0,
   nb_of_bids    INTEGER DEFAULT 0,
   max_bid       FLOAT DEFAULT 0,
   start_date    TIMESTAMP,
   end_date      TIMESTAMP,
   seller        INTEGER NOT NULL,
   category      INTEGER NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE old_items (
   id            SERIAL,
   name          VARCHAR(100),
   description   TEXT,
   initial_price FLOAT NOT NULL,
   quantity      INTEGER NOT NULL,
   reserve_price FLOAT DEFAULT 0,
   buy_now       FLOAT DEFAULT 0,
   nb_of_bids    INTEGER DEFAULT 0,
   max_bid       FLOAT DEFAULT 0,
   start_date    TIMESTAMP,
   end_date      TIMESTAMP,
   seller        INTEGER NOT NULL,
   category      INTEGER NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE bids (
   id      SERIAL,
   user_id INTEGER NOT NULL,
   item_id INTEGER NOT NULL,
   qty     INTEGER NOT NULL,
   bid     FLOAT NOT NULL,
   max_bid FLOAT NOT NULL,
   date    TIMESTAMP,
   PRIMARY KEY(id)
);

CREATE TABLE comments (
   id           SERIAL,
   from_user_id INTEGER NOT NULL,
   to_user_id   INTEGER NOT NULL,
   item_id      INTEGER NOT NULL,
   rating       INTEGER,
   date         TIMESTAMP,
   comment      TEXT,
   PRIMARY KEY(id)
);

CREATE TABLE buy_now (
   id       SERIAL,
   buyer_id INTEGER NOT NULL,
   item_id  INTEGER NOT NULL,
   qty      INTEGER NOT NULL,
   date     TIMESTAMP,
   PRIMARY KEY(id)
);

CREATE TABLE ids (
   id        INTEGER NOT NULL UNIQUE,
   category  INTEGER NOT NULL,
   region    INTEGER NOT NULL,
   users     INTEGER NOT NULL,
   item      INTEGER NOT NULL,
   comment   INTEGER NOT NULL,
   bid       INTEGER NOT NULL,
   buyNow    INTEGER NOT NULL,
   PRIMARY KEY(id)
);
