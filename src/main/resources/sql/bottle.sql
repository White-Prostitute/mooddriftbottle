create table bottle
(
    bottle_id   int auto_increment,
    user_id     varchar(50)       null,
    content     varchar(512)      null,
    checked     tinyint(1)        null,
    create_time datetime          null,
    type        int               null,
    logic       tinyint default 0 null,
    constraint bottle_bottle_id_uindex
        unique (bottle_id)
);

alter table bottle
    add primary key (bottle_id);

create table reply
(
    reply_id    int auto_increment
        primary key,
    content     varchar(256) null,
    create_time datetime     null,
    session_id  int          null,
    user_id     varchar(32)  null
);

create table session
(
    session_id          int auto_increment
        primary key,
    first_sentence      varchar(256)  null,
    session_create_time datetime      null,
    checked             tinyint       null,
    bottle_id           int           not null,
    logic               int default 0 null,
    user_id             varchar(32)   not null
);

create table users
(
    open_id     varchar(32)   not null,
    nick_name   varchar(16)   not null,
    salvage_num int default 0 null,
    violation   int default 0 null,
    constraint users_user_id_uindex
        unique (open_id)
);

alter table users
    add primary key (open_id);

ALTER TABLE users ADD score INT DEFAULT 0;

