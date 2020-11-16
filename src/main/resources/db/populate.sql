DELETE
FROM restaurants;
DELETE
FROM dishes;
DELETE
FROM menu;
DELETE
FROM users;
DELETE
FROM vote;

ALTER SEQUENCE vote_seq RESTART With 1000;
ALTER SEQUENCE menu_seq RESTART WITH 1000;
ALTER SEQUENCE user_seq RESTART WITH 10000;
ALTER SEQUENCE dish_seq RESTART WITH 100000;

INSERT INTO users(id, name, password, role)
VALUES (1, '1_Admin', '{noop}admin', 'ADMIN'),
       (2, '2_User', '{noop}password', 'USER'),
       (3, '3_User', '{noop}120168', 'USER');

INSERT INTO restaurants(name)
values ('McDnlds'),
       ('Godzik'),
       ('BurgerQueen');

INSERT INTO menu(id, date, restaurant)
VALUES (11, CURRENT_DATE - INTERVAL '1' DAY, 'McDnlds'),
       (12, CURRENT_DATE, 'McDnlds'),
       (21, CURRENT_DATE - INTERVAL '1' DAY, 'Godzik'),
       (22, CURRENT_DATE, 'Godzik'),
       (31, CURRENT_DATE - INTERVAL '1' DAY, 'BurgerQueen');

UPDATE restaurants SET menu = 12 WHERE name = 'McDnlds';
UPDATE restaurants SET menu = 22 WHERE name = 'Godzik';
UPDATE restaurants SET menu = 31 WHERE name = 'BurgerQueen';

INSERT INTO dishes(id, name, price, menu_id)
VALUES (111, 'Cucumber', 2, 11),
       (112, 'Orange', 6, 11),
       (113, 'Pizza', 15, 11),
       (114, 'Tomato', 3, 11),

       (121, 'Burger', 10, 12),
       (122, 'Pizza', 16, 12),
       (123, 'Pizza XXL', 20, 12),
       (124, 'surprise', 5, 12),
       (125, 'unknown', 50, 12),

       (211, 'Burger', 9, 21),
       (212, 'Chicken', 10, 21),
       (213, 'Pizza', 14, 21),

       (221, 'Chicken', 10, 22),
       (222, 'Fish', 8, 22),
       (223, 'Mice', 7, 22),

       (311, 'Burger', 4, 31),
       (312, 'Burger K-K-Kombo', 99, 31),
       (313, 'Pizza ;)', 10, 31),
       (314, 'Rat', 14, 31);

INSERT INTO vote(id, menu_id, user_id)
VALUES (101, 21, 2),
       (102, 21, 3),
       (103, 22, 2),
       (104, 12, 3);