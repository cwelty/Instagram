DROP TABLE IF EXISTS Photo;
DROP TABLE IF EXISTS Comments;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Rating;
DROP TABLE IF EXISTS PostRatings;
DROP TABLE IF EXISTS PhotoTags;
DROP TABLE IF EXISTS UserTags;
DROP TABLE IF EXISTS Followings;


-- Entities

CREATE TABLE Photo (
    parent_id NUMERIC(4) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    title VARCHAR(64) NOT NULL,
    rating NUMERIC(5) NOT NULL,
    dates VARCHAR(20) NOT NULL,
    time_stamp NUMERIC(4) NOT NULL,
    --image NUMERIC(4) NOT NULL,
    views NUMERIC(4) NOT NULL,
    PRIMARY KEY(parent_id)
);

INSERT INTO Photo VALUES(1, 'carson', 'poopy', 1, '12/12/2020', 1230, 1);
INSERT INTO Photo VALUES(2, 'preet', 'peepy', 2, '12/13/2020', 1130, 0);


CREATE TABLE Comments (
    parent_id NUMERIC(4) NOT NULL,
    comment_id NUMERIC(4) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    PRIMARY KEY(comment_id)
);

CREATE TABLE Users (
    user_id VARCHAR(64) NOT NULL,
    password VARCHAR(64) NOT NULL,
    follow_count NUMERIC(4) NOT NULL,
    PRIMARY KEY(user_id)
);

INSERT INTO Users VALUES('carson', '222', 0);
INSERT INTO Users VALUES('preet', '444', 0);
INSERT INTO Users VALUES('david', '777', 1);
INSERT INTO Users VALUES('clee', '888', 1);


CREATE TABLE Rating (
    parent_id NUMERIC(4) NOT NULL,
    rating_id NUMERIC(4) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    rating_type VARCHAR(64) NOT NULL,
    PRIMARY KEY(rating_id)
);

INSERT INTO Rating VALUES(1, 1, 'carson', 'like');
INSERT INTO Rating VALUES(2, 2, 'preet', 'like');


CREATE TABLE PostRatings (
    parent_id NUMERIC(4) NOT NULL,
    post_rate_id NUMERIC(4) NOT NULL,
    likes NUMERIC(4) NOT NULL,
    dislikes NUMERIC(4) NOT NULL,
    PRIMARY KEY(post_rate_id)
);

CREATE TABLE PhotoTags (
    parent_id NUMERIC(4) NOT NULL,
    photo_tag_id NUMERIC(4) NOT NULL,
    tag VARCHAR(64) NOT NULL,
    PRIMARY KEY(photo_tag_id)
);

INSERT INTO PhotoTags VALUES(1, 1, 'smelly');
INSERT INTO PhotoTags VALUES(2, 2, 'wowie');


CREATE TABLE UserTags (
    parent_id NUMERIC(4) NOT NULL,
    user_tag_id NUMERIC(4) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    PRIMARY KEY(user_tag_id)
);

INSERT INTO UserTags VALUES(1, 1, 'carson');
INSERT INTO UserTags VALUES(2, 2, 'preet');


CREATE TABLE Followings (
    following_id NUMERIC(4) NOT NULL,
    follower VARCHAR(64) NOT NULL,
    follows VARCHAR(64) NOT NULL,
    PRIMARY KEY(following_id)
);

INSERT INTO Followings VALUES(1, 'carson', 'clee');
INSERT INTO Followings VALUES(2, 'preet', 'david');

-- Relations


----------------------------
-- INSERT DATA STATEMENTS --
----------------------------

/*
COPY Photo (
    parent_id,
    user_id,
    title,
    rating,
    dates,
    time_stamp,
    views
)
FROM 'Photos.csv'
WITH DELIMITER ',';

COPY Rating (
    parent_id,
    rating_id,
    user_id,
    rating_type
)
FROM 'Ratings.csv'
WITH DELIMITER ',';

COPY Users (
    user_id,
    password,
    follow_count
)
FROM 'Users.csv'
WITH DELIMITER ',';

COPY Followings (
    following_id,
    follower,
    follows
)
FROM 'Followings.csv'
WITH DELIMITER ',';

COPY PhotoTags (
    parent_id,
    photo_tag_id,
    tag
)
FROM 'PhotoTags.csv'
WITH DELIMITER ',';
*/