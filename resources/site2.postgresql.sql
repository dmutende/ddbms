create database transy_db
	with owner elon;

create sequence routes_seq;

alter sequence routes_seq owner to elon;

create sequence drivers_seq;

alter sequence drivers_seq owner to elon;

create table if not exists routes
(
	route_id integer default nextval('routes_seq'::regclass) not null
		constraint routes_pk
			primary key,
	route_name varchar(150) not null,
	route_description varchar(255),
	origin_branch_id integer not null,
	destination_branch_id integer not null,
	created timestamp default now() not null,
	updated timestamp default now() not null
);

alter table routes owner to elon;

create unique index if not exists routes_route_name_uindex
	on routes (route_name);

create table if not exists drivers
(
	driver_id integer default nextval('drivers_seq'::regclass) not null
		constraint drivers_pk
			primary key,
	driver_first_name varchar(50) not null,
	driver_last_name varchar(50) not null,
	driver_other_names varchar(255),
	driver_national_id_number varchar(20) not null,
	driver_driving_license_number varchar(20) not null,
	driver_email_address varchar(50) not null,
	driver_phone_number varchar(50) not null,
	registration_branch_id integer not null,
	created timestamp default now() not null,
	updated timestamp default now() not null
);

alter table drivers owner to elon;

create unique index if not exists drivers_driver_national_id_number_uindex
	on drivers (driver_national_id_number);

create unique index if not exists drivers_driver_driving_license_number_uindex
	on drivers (driver_driving_license_number);

create unique index if not exists drivers_driver_email_address_uindex
	on drivers (driver_email_address);

create unique index if not exists drivers_driver_phone_number_uindex
	on drivers (driver_phone_number);

-- insert data
INSERT INTO public.drivers (driver_id, driver_first_name, driver_last_name, driver_other_names, driver_national_id_number, driver_driving_license_number, driver_email_address, driver_phone_number, registration_branch_id, created, updated) VALUES (6, 'Isaac', 'Kiptoo', 'Mulwa', '33989992', 'FR678JK899', 'ikiptoo@outlook.com', '254789333092', 2, '2019-06-10 13:39:55.140000', '2019-06-10 13:39:55.140000');
INSERT INTO public.drivers (driver_id, driver_first_name, driver_last_name, driver_other_names, driver_national_id_number, driver_driving_license_number, driver_email_address, driver_phone_number, registration_branch_id, created, updated) VALUES (7, 'Carlton', 'Moseti', null, '32439847', 'RJ878FJJ89', 'carlmoseti@gmail.com', '254712333099', 2, '2019-06-10 13:42:57.633000', '2019-06-10 13:42:57.633000');

INSERT INTO public.routes (route_id, route_name, route_description, origin_branch_id, destination_branch_id, created, updated) VALUES (9, 'MSA-KSM', 'Mombasa to Kisumu', 2, 3, '2019-06-10 15:26:33.100000', '2019-06-10 15:26:33.100000');
INSERT INTO public.routes (route_id, route_name, route_description, origin_branch_id, destination_branch_id, created, updated) VALUES (10, 'MSA(CBD)-MSA(NYALI BRIDGE)', 'Mombasa (CBD) to Mombasa (Nyali Bridge)', 2, 2, '2019-06-10 15:27:11.807000', '2019-06-10 15:27:11.807000');
INSERT INTO public.routes (route_id, route_name, route_description, origin_branch_id, destination_branch_id, created, updated) VALUES (8, 'MSA-NBO', 'Mombasa to Nairobi', 2, 1, '2019-06-10 15:26:11.197000', '2019-06-10 15:26:11.197000');