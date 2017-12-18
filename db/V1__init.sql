DROP TABLE IF EXISTS route;
DROP TABLE IF EXISTS mountain;
DROP TABLE IF EXISTS climbing_person;
DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS climbing;

CREATE TABLE person (
  id            SERIAL,
  username      VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  full_name     VARCHAR(255) NOT NULL,
  level         VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE mountain (
  id     SERIAL,
  name   VARCHAR(255) NOT NULL,
  height FLOAT        NOT NULL,
  lat    FLOAT        NOT NULL,
  lon    FLOAT        NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE route
(
  id          SERIAL PRIMARY KEY,
  name        VARCHAR(255) NOT NULL,
  mountain_id SERIAL       NOT NULL,
  CONSTRAINT route_mountain_id_fk FOREIGN KEY (mountain_id) REFERENCES mountain (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE climbing (
  id           SERIAL,
  start_time   TIMESTAMP    NOT NULL,
  end_time     TIMESTAMP    NOT NULL,
  mountain_id  SERIAL,
  status       VARCHAR(100) NOT NULL,
  min_level    VARCHAR(100) NOT NULL,
  person_limit INTEGER      NOT NULL,
  route_id     SERIAL       NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (mountain_id) REFERENCES mountain (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (route_id) REFERENCES route (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE climbing_person (
  climbing_id SERIAL,
  person_id   SERIAL,
  PRIMARY KEY (climbing_id, person_id),
  FOREIGN KEY (climbing_id) REFERENCES climbing (id) ON DELETE NO ACTION ON UPDATE CASCADE,
  FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE NO ACTION ON UPDATE CASCADE
);