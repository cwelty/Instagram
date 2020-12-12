DROP TABLE IF EXISTS Photo;
DROP TABLE IF EXISTS Comments;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Rating;
DROP TABLE IF EXISTS Tag;

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

CREATE TABLE Rating (
    parent_id NUMERIC(4) NOT NULL,
    rating_id NUMERIC(4) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    rating_type VARCHAR(64) NOT NULL,
    PRIMARY KEY(rating_id)
);

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

CREATE TABLE UserTags (
    parent_id NUMERIC(4) NOT NULL,
    user_tag_id NUMERIC(4) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    PRIMARY KEY(user_tag_id)
);

CREATE TABLE Followings (
    following_id NUMERIC(4) NOT NULL,
    follower VARCHAR(64) NOT NULL,
    follows VARCHAR(64) NOT NULL,
    PRIMARY KEY(following_id)
);

-- Relations


----------------------------
-- INSERT DATA STATEMENTS --
----------------------------


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

/*
COPY Comments (
    comment_id,
    content,
    user_id,
    parent_id
)
FROM 'Comments.csv'
WITH DELIMITER ',';





COPY Tag  (
    tag_id,
    tag,
    parent_id
)
--FROM 'Tag.csv'
WITH DELIMITER ',';
*/

COPY PhotoTags (
    parent_id,
    photo_tag_id,
    tag
)
FROM 'PhotoTags.csv'
WITH DELIMITER ',';