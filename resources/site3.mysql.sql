create database if not exists transy_db collate latin1_swedish_ci;

create table if not exists drivers
(
	driver_id int auto_increment
		primary key,
	driver_first_name varchar(50) charset utf8 not null,
	driver_last_name varchar(50) charset utf8 not null,
	driver_other_names varchar(255) charset utf8 null,
	driver_national_id_number varchar(20) not null,
	driver_driving_license_number varchar(20) not null,
	driver_email_address varchar(50) charset utf8 not null,
	driver_phone_number varchar(50) not null,
	registration_branch_id int not null,
	created datetime default CURRENT_TIMESTAMP not null,
	updated datetime default CURRENT_TIMESTAMP not null,
	constraint drivers_driver_driving_license_number_uindex
		unique (driver_driving_license_number),
	constraint drivers_driver_email_address_uindex
		unique (driver_email_address),
	constraint drivers_driver_national_id_number_uindex
		unique (driver_national_id_number),
	constraint drivers_driver_phone_number_uindex
		unique (driver_phone_number)
);

create table if not exists routes
(
	route_id int auto_increment
		primary key,
	route_name varchar(150) charset utf8 not null,
	route_description varchar(255) charset utf8 null,
	origin_branch_id int not null,
	destination_branch_id int not null,
	created datetime default CURRENT_TIMESTAMP not null,
	updated datetime default CURRENT_TIMESTAMP not null,
	constraint routes_route_name_uindex
		unique (route_name)
);

-- insert data
INSERT INTO transy_db.drivers (driver_id, driver_first_name, driver_last_name, driver_other_names, driver_national_id_number, driver_driving_license_number, driver_email_address, driver_phone_number, registration_branch_id, created, updated) VALUES (5, 'Gabriel', 'Sakali', 'Irungu', '32498793', 'Q34998IO78', 'gsakali@gmail.com', '254723999082', 3, '2019-06-10 13:41:10', '2019-06-10 13:41:10');

INSERT INTO transy_db.routes (route_id, route_name, route_description, origin_branch_id, destination_branch_id, created, updated) VALUES (5, 'KSM-NBO', 'Kisumu to Nairobi', 3, 1, '2019-06-10 15:26:11', '2019-06-10 15:26:11');
INSERT INTO transy_db.routes (route_id, route_name, route_description, origin_branch_id, destination_branch_id, created, updated) VALUES (6, 'KSM-MSA', 'Kisumu to Mombasa', 3, 2, '2019-06-10 15:26:33', '2019-06-10 15:26:33');
INSERT INTO transy_db.routes (route_id, route_name, route_description, origin_branch_id, destination_branch_id, created, updated) VALUES (7, 'KSM(CBD)-KSM(NYALENDA)', 'Kisumu (CBD) to Kisumu (Nyalenda)', 3, 3, '2019-06-10 15:27:12', '2019-06-10 15:27:12');