create table airtable_database
(
    id      varchar(100) not null primary key,
    name    varchar(100) not null,
    permission_level varchar(100) not null,
    actual_start_date date not null,
    next_base_id varchar(100) default null,
    full_processed boolean default false,
    created_date timestamp default null,
    last_updated_date timestamp default null,
    constraint fk_next_base_id foreign key (next_base_id) references airtable_database (id)
);

create unique index uidx_airtable_base_next_base_id on airtable_database (COALESCE(next_base_id, 'NULL'));