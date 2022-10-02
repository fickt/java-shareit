DROP TABLE IF EXISTS ITEM_TABLE CASCADE;
DROP TABLE IF EXISTS USER_TABLE CASCADE;
DROP TABLE IF EXISTS BOOKING_TABLE CASCADE;
DROP TABLE IF EXISTS COMMENT_TABLE CASCADE;
DROP TABLE IF EXISTS REQUEST_TABLE CASCADE;
DROP TABLE IF EXISTS REQUEST_ITEM_TABLE CASCADE;
DROP TABLE IF EXISTS ITEM_COMMENT_TABLE CASCADE;

CREATE TABLE IF NOT EXISTS USER_TABLE
(
    ID    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    NAME  VARCHAR(100),
    EMAIL VARCHAR(100),
    CONSTRAINT UNIQUE_EMAIL UNIQUE (EMAIL)
);

CREATE TABLE IF NOT EXISTS ITEM_TABLE
(
    ID           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    NAME         VARCHAR(100),
    DESCRIPTION  TEXT,
    IS_AVAILABLE BOOLEAN,
    OWNER_ID     BIGINT DEFAULT NULL,
    REQUEST_ID   BIGINT DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS BOOKING_TABLE
(
    ID         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    START_DATE DATETIME,
    END_DATE   DATETIME,
    ITEM_ID    BIGINT,
    USER_ID    BIGINT,
    STATUS     VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS COMMENT_TABLE
(
    ID          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    CONTENT     TEXT,
    ITEM_ID     BIGINT,
    AUTHOR_ID   BIGINT,
    AUTHOR_NAME VARCHAR(64),
    CREATED     DATETIME
);

CREATE TABLE IF NOT EXISTS ITEM_COMMENT_TABLE
(
    ITEM_ID    BIGINT,
    COMMENT_ID BIGINT,
    FOREIGN KEY (ITEM_ID) REFERENCES ITEM_TABLE (ID) ON DELETE CASCADE,
    FOREIGN KEY (COMMENT_ID) REFERENCES COMMENT_TABLE (ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REQUEST_TABLE
(
    ID           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    DESCRIPTION  TEXT,
    REQUESTOR_ID BIGINT,
    CREATED_AT   DATETIME
);

CREATE TABLE IF NOT EXISTS REQUEST_ITEM_TABLE
(
    REQUEST_ID BIGINT,
    ITEM_ID    BIGINT,
    FOREIGN KEY (REQUEST_ID) REFERENCES REQUEST_TABLE(ID),
    FOREIGN KEY (ITEM_ID) REFERENCES ITEM_TABLE(ID)
);