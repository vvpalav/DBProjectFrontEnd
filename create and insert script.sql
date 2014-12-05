drop table if exists concert_attendance_log;
drop table if exists user_comments;
drop table if exists system_created_concert_info;
drop table if exists users_music_choice;
drop table if exists user_created_concert_info;
drop table if exists user_to_user_follow;
drop table if exists user_to_artist_follow;
drop table if exists artist_music_skills;
drop table if exists sub_genre;
drop table if exists main_genre;
drop table if exists artist_info;
drop table if exists user_concert_list;
drop table if exists company_info;
drop table if exists venue_info;
drop table if exists user_info;

create table user_info (
    uid             varchar(20)     primary key,
    password        varchar(20),
    first_name      varchar(20),
    last_name       varchar(20),
    dob             date,
    email           varchar(50),
    city            varchar(20),
    user_repo       integer,
    last_acc_date   date,
    reg_date        date
);
  
create table venue_info (
    vid         varchar(20)     primary key,
    vname        varchar(20),
    street      varchar(20),
    city        varchar(20),
    state       varchar(20),
    country     varchar(20),
    zip         integer
);

create table company_info (
    com_id      varchar(20)     primary key,
    com_name    varchar(30)
);

create table user_concert_list (
    uid             varchar(20),
    sys_con_id          varchar(20),
    user_con_id     varchar(20),
    attending       varchar(10),
    primary key (uid, sys_con_id, user_con_id),
    foreign key (uid) references user_info(uid)
);

create table main_genre (
    mgid         varchar(20)     primary key,
    g_desc      varchar(50)
);

create table sub_genre (
    sgid        varchar(20),
    sg_desc     varchar(50),
    mgid         varchar(20),
    primary key (mgid, sgid),
    foreign key (mgid) references main_genre(mgid)
);

create table artist_info (					
    aid         varchar(20)     primary key,
    password    varchar(20)     not null,
    aname       varchar(20),
    webpage     varchar(200),
    com_id      varchar(20),
    foreign key (com_id) references company_info(com_id)
);

create table system_created_concert_info (
    sys_con_id                  varchar(20)     primary key,
    sys_con_name                varchar(20),
    vid                         varchar(20),
    sys_con_time                datetime,
    artist                      varchar(20),
    company                     varchar(20),
    capacity                    integer,
    avail_tickets               integer,
    price                       integer,
    hyperlink                   varchar(100),
    posted_date                 datetime,
    concert_genre               varchar(20),
    foreign key (vid)           references venue_info(vid),
    foreign key (artist)        references artist_info(aid),
    foreign key (company)       references company_info(com_id),
    foreign key (concert_genre) references main_genre(mgid)
);

create table user_created_concert_info (
    user_con_id         varchar(20)     primary key,
    user_con_name       varchar(20),
    user_con_time       datetime,
    vid                 varchar(20),
    artist              varchar(20),
    created_by_uid      varchar(20),
    created_date        datetime,
    foreign key (vid)   references venue_info (vid),
    foreign key (artist)   references artist_info (aid),
    foreign key (created_by_uid) references user_info (uid)
);

create table user_comments (							
    post_id         varchar(20)     primary key,
    uid             varchar(20),
    sys_con_id      varchar(20),
    user_con_id     varchar(20),
    text            varchar(300),
    posted_on       datetime,
    foreign key (uid) references user_info (uid),
    foreign key (sys_con_id) references system_created_concert_info (sys_con_id),
    foreign key(user_con_id) references user_created_concert_info (user_con_id)
);

create table user_to_user_follow (
    my_uid          varchar(20),
    following_uid   varchar(20),
    from_date       datetime,
    primary key(my_uid, following_uid),
    foreign key(my_uid) references user_info (uid),
    foreign key(following_uid) references user_info (uid)
);

create table user_to_artist_follow (
    my_uid          varchar(20),
    following_aid   varchar(20),
    from_date       datetime,
    primary key(my_uid, following_aid),
    foreign key(my_uid) references user_info (uid),
    foreign key(following_aid) references artist_info (aid)
);

create table artist_music_skills (
    aid         varchar(20),
    mgid         varchar(20),
    sgid        varchar(20),
    primary key(aid, mgid, sgid),
    foreign key(aid) references artist_info (aid),
    foreign key(mgid) references main_genre (mgid)
);

create table users_music_choice (
    uid         varchar(20),
    mgid        varchar(20),
    sgid        varchar(20),
    primary key(uid, mgid, sgid),
    foreign key(uid) references user_info (uid),
    foreign key(mgid) references main_genre(mgid)
);

create table concert_attendance_log (
    uid         varchar(20),
    sys_con_id  varchar(20),
    user_con_id varchar(20),
    rating      integer,
    attended_on datetime,
    primary key(uid, attended_on),
    foreign key(uid) references user_info (uid),
    foreign key(sys_con_id) references system_created_concert_info (sys_con_id),
    foreign key(user_con_id) references user_created_concert_info (user_con_id)
);

insert into user_info values ('hardikgohil123','pass123', 'Hardik','Gohil',05/28/1990,
    'hcgfsa235@nyu.edu','Brooklyn','0',current_timestamp(),current_timestamp());
insert into user_info values ('vinayakpalav123', 'pass123', 'Vinayak','Palav',07/30/1999,
    'adsda299@nyu.edu','Brooklyn','0',current_timestamp(),current_timestamp());
insert into user_info values ('palavvinayak123', 'pass123', 'Palav','Vinayak',07/30/1999,
    'acbfsa@nyu.edu','Brooklyn','0',current_timestamp(),current_timestamp());
insert into user_info values ('gohilhardik123', 'pass123', 'Gohil','Hardik',05/28/1990,
    'ewfsfsf@gmail.com','Brooklyn','0',now(), now());

insert into main_genre values(1,'Jazz');
insert into main_genre values(2,'Pop');
insert into main_genre values(3,'Rock');
insert into main_genre values(4,'Classic');

insert into sub_genre values(11,'Free Jazz',1);
insert into sub_genre values(12,'Swing Jazz',1);
insert into sub_genre values(13,'Acid Jazz',1);
insert into sub_genre values(14,'Walt Jazz',1);
insert into sub_genre values(21,'Indie Pop',2);
insert into sub_genre values(22,'C Pop',2);
insert into sub_genre values(23,'Indian Pop',2);
insert into sub_genre values(24,'Latin Pop',2);
insert into sub_genre values(31,'Hard Rock',3);
insert into sub_genre values(33,'College Rock',3);
insert into sub_genre values(32,'Dark Rock',3);
insert into sub_genre values(34,'Indie Rock',3);
insert into sub_genre values(41,'Latin Classic',4);
insert into sub_genre values(42,'Gypsy Classic',4);
insert into sub_genre values(43,'Latin Classic',4);
insert into sub_genre values(44,'celtic Classic',4);

insert into company_info values(1,'Vergin Music');
insert into company_info values(2,'Vevo');
insert into company_info values(3,'MTV');
insert into company_info values(4,'Universal Studio');
insert into company_info values(5,'Sound Cloud');

insert into artist_info values ('artist1’, ’pass1234’, ’Linkinpark','http://www.linkinpark.com/',4);
insert into artist_info values ('artist2', ’pass1234’, 'John Legend','http://www.johnlegend.com/us/',1);
insert into artist_info values ('artist3', ’pass1234’, 'Avicii','http://avicii.com/',5);
insert into artist_info values ('artist4', ’pass1234’, 'Enriqu Eiglesias','http://www.enriqueiglesias.com/',4);

insert into venue_info values('venue1','Times Square', '42nd Street','Manhattan','NY','USA','10036');
insert into venue_info values('venue2','Barclays Center', 'Atalantic Avenue','Brooklyn','NY','USA','11217');
insert into venue_info values('venue3','Avenue of Americas', '1065 Av of Americas','Manhattan','NY','USA','10018');
insert into venue_info values('venue4','Bryant Park', '42nd Street','Manhattan','NY','USA','10018');

insert into users_music_choice values('vinayakpalav123','1','11');
insert into users_music_choice values('vinayakpalav123','1','12');
insert into users_music_choice values('vinayakpalav123','1','13');
insert into users_music_choice values('vinayakpalav123','1','14');
insert into users_music_choice values('hardikgohil123','2','21');
insert into users_music_choice values('hardikgohil123','2','22');
insert into users_music_choice values('palavvinayak123','1','11');
insert into users_music_choice values('palavvinayak123','2','21');
insert into users_music_choice values('palavvinayak123','3','31');
insert into users_music_choice values('palavvinayak123','3','32');
insert into users_music_choice values('palavvinayak123','3','33');
insert into users_music_choice values('palavvinayak123','3','34');
insert into users_music_choice values('gohilhardik123','1','11');
insert into users_music_choice values('gohilhardik123','1','21');
insert into users_music_choice values('gohilhardik123','1','31');
insert into users_music_choice values('gohilhardik123','4','41');

insert into artist_music_skills values('artist1','1','11');
insert into artist_music_skills values('artist1','1','12');
insert into artist_music_skills values('artist1','1','13');
insert into artist_music_skills values('artist1','1','14');
insert into artist_music_skills values('artist2','3','31');
insert into artist_music_skills values('artist2','3','32');
insert into artist_music_skills values('artist2','3','33');
insert into artist_music_skills values('artist2','3','34');
insert into artist_music_skills values('artist3','1','11');
insert into artist_music_skills values('artist3','1','12');
insert into artist_music_skills values('artist3','1','13');
insert into artist_music_skills values('artist3','1','14');
insert into artist_music_skills values('artist4','4','11');
insert into artist_music_skills values('artist4','4','12');
insert into artist_music_skills values('artist4','4','13');
insert into artist_music_skills values('artist4','4','14');

insert into system_created_concert_info values('concert1','Linkin Park Concert', 'venue1',
'2014-11-27 18:00:00','artist1', null, 100,100,50,'http://www.linkinpark.com/', '2014-11-20','1');
insert into system_created_concert_info values('concert2','Linkin Park Concert', 'venue1',
'2012-01-01 18:00:00','artist1', null, 100,100,50,'http://www.linkinpark.com/', '2014-11-20','2');
insert into system_created_concert_info values('concert3','Avicii concert', 'venue3',
'2014-12-31 18:00:00','artist3', null, 100,100,50,'http://avicii.com/', '2014-11-20','1');
insert into system_created_concert_info values('concert4','Avicii concert', 'venue3',
'2014-10-31 18:00:00','artist3', null, 100,55,50,'http://avicii.com/','2014-11-20', '1');

insert into concert_attendance_log values('vinayakpalav123','concert1',null, '6', now());
insert into concert_attendance_log values('palavvinayak123','concert2', null, '8',now());
insert into concert_attendance_log values('gohilhardik123','concert3', null, '0', now());
insert into concert_attendance_log values('hardikgohil123','concert4', null, '0', now());

insert into user_to_artist_follow values('vinayakpalav123','artist1',now());
insert into user_to_artist_follow values('vinayakpalav123','artist2',now());
insert into user_to_artist_follow values('palavvinayak123','artist1',now());
insert into user_to_artist_follow values('palavvinayak123','artist4',now());
insert into user_to_artist_follow values('gohilhardik123','artist1',now());
insert into user_to_artist_follow values('gohilhardik123','artist4',now());
insert into user_to_artist_follow values('gohilhardik123','artist3',now());
insert into user_to_artist_follow values('gohilhardik123','artist2',now());
insert into user_to_artist_follow values('hardikgohil123','artist2',now());

insert into user_to_user_follow values('vinayakpalav123','palavvinayak123',now());
insert into user_to_user_follow values('vinayakpalav123','hardikgohil123',now());
insert into user_to_user_follow values('palavvinayak123','hardikgohil123',now());
insert into user_to_user_follow values('palavvinayak123','gohilhardik123',now());
insert into user_to_user_follow values('hardikgohil123','gohilhardik123',now());

insert into user_comments values('concert1','vinayakpalav123','concert4', null,
    'Excellent performance!',now());
insert into user_comments values('concert2','palavvinayak123','concert4', null, 
    'Not so good',now());
insert into user_comments values('concert3','gohilhardik123','concert4', null,
    'Not worth of the cost',now());

commit;