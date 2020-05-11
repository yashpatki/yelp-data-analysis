CREATE TABLE USERS 
(user_id VARCHAR2(50),
name VARCHAR2(50), 
votes_funny int, 
votes_useful int, 
votes_cool int, 
review_count int, 
average_stars float);



CREATE TABLE REVIEWS
(business_id VARCHAR2(50),
user_id VARCHAR2(50),
review_id VARCHAR2(50),
text varchar2(3500 char),
date1 varchar2(10),
stars float,
votes_funny int, 
votes_useful int, 
votes_cool int)

CREATE TABLE BUSINESS
(business_id VARCHAR2(200),
name VARCHAR2(150),
city VARCHAR2(150),
full_address VARCHAR2(500),
state VARCHAR2(150),
review_count int,
stars float) 

CREATE TABLE HOURS(
day VARCHAR2(50),
open float,
close float,
business_id VARCHAR2(50)
)


CREATE TABLE MAIN_CATEGORY(
CATEGORY VARCHAR2(50),
BUSINESS_ID VARCHAR2(50))


CREATE TABLE SUB_CATEGORY(CATEGORY VARCHAR2(50),
BUSINESS_ID VARCHAR2(50))

CREATE TABLE ATTRIBUTES(A_VALUE VARCHAR2(50),business_id VARCHAR2(50))