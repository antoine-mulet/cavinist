--
-- Creation of applicative user
--

DO
$do$
    BEGIN
        IF NOT EXISTS(SELECT FROM pg_user WHERE usename = '${app-user}') THEN
            CREATE USER ${app-user} WITH NOSUPERUSER NOCREATEDB NOCREATEROLE ENCRYPTED PASSWORD '${app-password}';
        END IF;
    END
$do$;

REVOKE ALL ON DATABASE ${db-name} FROM PUBLIC;

GRANT CONNECT ON DATABASE ${db-name} TO ${app-user};
GRANT USAGE ON SCHEMA ${app-schema} TO ${app-user};
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA ${app-schema} TO ${app-user};
GRANT SELECT, USAGE ON ALL SEQUENCES IN SCHEMA ${app-schema} TO ${app-user};
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA ${app-schema} TO ${app-user};
ALTER DEFAULT PRIVILEGES IN SCHEMA ${app-schema} GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO ${app-user};
ALTER DEFAULT PRIVILEGES IN SCHEMA ${app-schema} GRANT SELECT, USAGE ON SEQUENCES TO ${app-user};
ALTER DEFAULT PRIVILEGES IN SCHEMA ${app-schema} GRANT EXECUTE ON FUNCTIONS TO ${app-user};

--
-- Cavinist Database schema
--

create table users
(
    id       uuid primary key,
    login    varchar(50)  not null,
    password varchar(100) not null,
    unique (login)
);

create table regions
(
    id      uuid primary key,
    name    varchar(100) not null,
    country varchar(80)  not null,
    user_id uuid references users,
    version bigint,
    unique (name, country, user_id)
);

create table wineries
(
    id        uuid primary key,
    name      varchar(255)            not null,
    region_id uuid references regions not null,
    user_id   uuid references users,
    version   bigint                  not null,
    unique (name, region_id, user_id)
);

create table wines
(
    id        uuid primary key,
    name      varchar(255)             not null,
    type      int                      not null,
    winery_id uuid references wineries not null,
    region_id uuid references regions  not null,
    user_id   uuid references users,
    version   bigint                   not null,
    unique (name, type, winery_id, region_id, user_id)
);