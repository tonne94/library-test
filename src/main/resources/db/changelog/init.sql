--liquibase formatted sql

--changeset antonio:1
CREATE SEQUENCE ACCOUNT_SEQ;
CREATE TABLE account(
    id SERIAL PRIMARY KEY,
    name VARCHAR (50) NOT NULL,
    surname VARCHAR (50) NOT NULL,
    is_valid BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE SEQUENCE BOOK_SEQ;
CREATE TABLE book(
    id SERIAL PRIMARY KEY,
    title VARCHAR (50) NOT NULL
);

CREATE SEQUENCE AUTHOR_SEQ;
CREATE TABLE author(
    id SERIAL PRIMARY KEY,
    name VARCHAR (50) NOT NULL,
    surname VARCHAR (50) NOT NULL
);

CREATE TABLE book_author(
    book_id  int REFERENCES book (id) NOT NULL,
    author_id int REFERENCES author (id) NOT NULL
);

CREATE SEQUENCE BOOK_RECORD_SEQ;
CREATE TABLE book_record(
    id SERIAL PRIMARY KEY,
    book_id int REFERENCES book (id) NOT NULL,
    damaged BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE SEQUENCE RENT_RECORD_SEQ;
CREATE TABLE rent_record(
    id SERIAL PRIMARY KEY,
    book_record_id int REFERENCES book_record (id) NOT NULL,
    account_id int REFERENCES account (id) NOT NULL,
    rent_time TIMESTAMP,
    return_time TIMESTAMP,
    actual_return_time TIMESTAMP
);

--changeset antonio:2
CREATE SEQUENCE CONTACT_SEQ;
CREATE TABLE contact(
    id SERIAL PRIMARY KEY,
    account_id int REFERENCES account (id) NOT NULL,
    contact_type int NOT NULL,
    contact VARCHAR (50) NOT NULL
);

--changeset antonio:3
ALTER TABLE rent_record
ADD COLUMN overdue_days_paid BOOLEAN DEFAULT FALSE NOT NULL;

--changeset antonio:4
ALTER TABLE book_record
ADD COLUMN invalid BOOLEAN DEFAULT FALSE NOT NULL;