-- Drop tables if they exist (in reverse order due to foreign key constraints)
DROP TABLE IF EXISTS book_author;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS author;

-- Author table
CREATE TABLE author (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(255),
    last_name  VARCHAR(255)
);

-- Book table - the Aggregate-Root
CREATE TABLE book (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    isbn  VARCHAR(20)
);

-- Junction table managed by the Book aggregate
CREATE TABLE book_author (
    book_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE
);
