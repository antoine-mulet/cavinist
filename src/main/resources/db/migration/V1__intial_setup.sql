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

create table chateaux
(
    id     uuid primary key,
    name   varchar(255) not null,
    region varchar(100) not null
);

insert into chateaux(id, name, region)
values ('0d60a85e-0b90-4482-a14c-108aea2557aa', 'Chateau Petrus', 'Pomerol');

insert into chateaux(id, name, region)
values ('39240e9f-ae09-4e95-9fd0-a712035c8ad7', 'Le Clos des Fées', 'Roussillon');

create table wines
(
    id         uuid primary key,
    chateau_id uuid references chateaux,
    name       varchar(255) not null,
    year       int          not null
);

insert into wines(id, name, chateau_id, year)
values ('9e4de779-d6a0-44bc-a531-20cdb97178d2', 'Petrus', '0d60a85e-0b90-4482-a14c-108aea2557aa', 1993);

insert into wines(id, name, chateau_id, year)
values ('66a45c1b-19af-4ab5-8747-1b0e2d79339d', 'La Fleur Petrus', '0d60a85e-0b90-4482-a14c-108aea2557aa', 2002);

insert into wines(id, name, chateau_id, year)
values ('bc8250bb-f7eb-4adc-925c-2af315cc4a55', 'Les Sorcières', '39240e9f-ae09-4e95-9fd0-a712035c8ad7', 2017);