INSERT INTO person (id, username, password_hash, full_name, level) VALUES
  (1, 'ivanov', '60a48844468f587dbcf92f8eba976f392e450d64', 'Иванов Евгений', 'NEWBIE'),
  (2, 'kuzmin', '64832e81973163e6891ff1626bba6ab777b72580', 'Кузьмин Кирилл', 'SKILLED'),
  (3, 'getman', '26316ffb8e349cd2277463ccab6ad2b617094b57', 'Гетман Игорь ', 'NEWBIE '),
  (4, 'klecko', '0ad59482b460a24429618e40d73434e57d4a4d74', 'Клецко Константин', 'NEWBIE'),
  (5, 'budanov', '23ecdde07981909015e2cef133630ee86177dff9', 'Буданов Петр ', 'LEAD'),
  (6, 'gavrilov', 'be46bbe3c3ca6e6deffcc80b047ac0d9a16201c2', 'Гаврилов Борис ', 'NEWBIE'),
  (7, 'bogachev', '9b35fb5cca288a5a2f1b63a9945e5fba019b310d', 'Богачев Иван ', 'LEAD'),
  (8, 'sivcov', '3d739dee58733a12c91eeb2dd0d3344d02489ac6', 'Сивцов Борис ', 'NEWBIE');

ALTER SEQUENCE "person_id_seq" RESTART WITH 9;

INSERT INTO mountain VALUES
  (1, 'Эверест', 8848, 27.59, 86.55),
  (2, 'Пик Победы', 7439, 42.03, 80.11),
  (3, 'Аконкагуа', 6961, 32.39, 70.55),
  (4, 'Денали', 6190, 63.04, 151),
  (5, 'Килиманджаро', 5895, 3.04, 37.21);

ALTER SEQUENCE "mountain_id_seq" RESTART WITH 6;

INSERT INTO climbing VALUES
  (1, '2017-01-03', '2017-01-12', 2, 'SUCCESS', 'SKILLED', 5),
  (2, '2017-09-12', '2017-09-13', 3, 'FAIL', 'NEWBIE', 4);
ALTER SEQUENCE climbing_id_seq RESTART WITH 3;

INSERT INTO climbing_person VALUES
  (1, 1),
  (1, 2),
  (1, 3),
  (1, 4),
  (2, 5),
  (2, 6),
  (2, 7);

INSERT INTO route
VALUES
  (1, 'route1', 1),
  (2, 'route2', 1),
  (3, 'route3', 1),
  (4, 'route4', 2),
  (5, 'route5', 2),
  (6, 'route6', 2),
  (7, 'route7', 3),
  (8, 'route8', 4),
  (9, 'route9', 5);
