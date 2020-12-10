DROP TABLE IF EXISTS Photo;
DROP TABLE IF EXISTS Comments;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Rating;
DROP TABLE IF EXISTS Tag;

-- Entities

CREATE TABLE Photo (
    photo_id VARCHAR(64) NOT NULL,
    parent_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    title VARCHAR(64) NOT NULL,
    ratings NUMERIC(5) NOT NULL,
    dates VARCHAR(20) NOT NULL,
    timer NUMERIC(4) NOT NULL,
    PRIMARY KEY(photo_id)
);

CREATE TABLE Comments (
    comment_id VARCHAR(64) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    parent_id VARCHAR(64) NOT NULL,
    PRIMARY KEY(comment_id)
);

CREATE TABLE Users (
    user_id VARCHAR(64) NOT NULL,
    password VARCHAR(64) NOT NULL,
    PRIMARY KEY(user_id)
);

CREATE TABLE Rating (
    rating_id VARCHAR(64) NOT NULL,
    rating_type VARCHAR(64) NOT NULL,
    parent_id VARCHAR(64) NOT NULL,
    PRIMARY KEY(rating_id)
);

CREATE TABLE Tag (
    tag_id VARCHAR(64) NOT NULL,
    tag VARCHAR(64) NOT NULL,
    parent_id VARCHAR(64) NOT NULL,
    PRIMARY KEY(tag_id)
);

-- Relations


----------------------------
-- INSERT DATA STATEMENTS --
----------------------------

COPY Photo (
	photo_id,
    parent_id,
    user_id,
	title,
	tags,
	ratings,
	dates,
	timer
)
--FROM 'Photos.csv'
WITH DELIMITER ',';

COPY Comments (
	comment_id,
	content,
    parent_id
)
--FROM 'Content.csv'
WITH DELIMITER ',';

COPY Users (
	user_id,
	password
)
--FROM 'Users.csv'
WITH DELIMITER ',';

COPY Rating (
	rating_id,
	rating_type,
    parent_id
)
--FROM 'Rating.csv'
WITH DELIMITER ',';

COPY Tag  (
	tag_id,
    tag,
    parent_id
)
--FROM 'Tag.csv'
WITH DELIMITER ',';
