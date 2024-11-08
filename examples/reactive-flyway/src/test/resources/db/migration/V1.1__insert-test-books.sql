CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO book (id, title, author)
VALUES (uuid_generate_v4(), 'The Catcher in the Rye', 'J.D. Salinger'),
       (uuid_generate_v4(), 'To Kill a Mockingbird', 'Harper Lee'),
       (uuid_generate_v4(), '1984', 'George Orwell'),
       (uuid_generate_v4(), 'Pride and Prejudice', 'Jane Austen'),
       (uuid_generate_v4(), 'The Great Gatsby', 'F. Scott Fitzgerald');
