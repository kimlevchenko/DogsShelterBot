-- liquibase formatted sql

--changeset levchenko:create_user
DROP TABLE IF EXISTS users;
CREATE TABLE users
( --имя user не разрешает, зарезервировано
    id                SERIAL PRIMARY KEY,
    name              VARCHAR(30) NOT NULL,
    shelter_id        VARCHAR(3),
    state_id          VARCHAR(30) NOT NULL,
    previous_state_id VARCHAR(30),
    state_time        TIMESTAMP,
    FOREIGN KEY (state_id) REFERENCES state (id),
    FOREIGN KEY (previous_state_id) REFERENCES state (id),
    FOREIGN KEY (shelter_id) REFERENCES shelter (id)
);
INSERT INTO users(id, name, shelter_id, state_id)
VALUES (11, 'User11', 'DOG', 'Shelter'),
       (22, 'User22', 'CAT', 'Shelter'),
       (340330886, 'Москва', Null, 'Shelter');