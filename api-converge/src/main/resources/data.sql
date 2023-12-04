CREATE SCHEMA dataset;
USE
dataset;

CREATE TABLE alert_datastore
(
    cap_id      VARCHAR(200)  NOT NULL PRIMARY KEY,
    cap_code    VARCHAR(200) DEFAULT NULL,
    gov_code    VARCHAR(200) DEFAULT NULL,
    county_code VARCHAR(200) DEFAULT NULL,
    effective   DATETIME     DEFAULT NULL,
    expires     DATETIME     DEFAULT NULL,
    description VARCHAR(2000) NOT NULL

);

