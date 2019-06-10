create database TransyDB;
go

use TransyDB;
go

create table branches
(
	branch_id int identity
		constraint branches_pk
			primary key nonclustered,
	branch_code varchar(50) not null,
	branch_name nvarchar(255) not null,
	branch_location nvarchar(1000) not null,
	created datetime default getdate() not null,
	updated datetime default getdate()
)
go

create unique index branches_branch_code_uindex
	on branches (branch_code)
go

create table customers
(
	customer_id int identity
		constraint customers_pk
			primary key nonclustered,
	customer_first_name nvarchar(50) not null,
	customer_last_name nvarchar(50) not null,
	customer_other_names nvarchar(255),
	customer_national_id_number varchar(20) not null,
	customer_email_address nvarchar(50) not null,
	customer_phone_number varchar(50) not null,
	created datetime default getdate() not null,
	updated datetime default getdate() not null
)
go

create unique index customers_customer_national_id_number_uindex
	on customers (customer_national_id_number)
go

create unique index customers_customer_email_address_uindex
	on customers (customer_email_address)
go

create unique index customers_customer_phone_number_uindex
	on customers (customer_phone_number)
go

create table drivers
(
	driver_id int identity
		constraint drivers_pk
			primary key nonclustered,
	driver_first_name nvarchar(50) not null,
	driver_last_name nvarchar(50) not null,
	driver_other_names nvarchar(255),
	driver_national_id_number varchar(20) not null,
	driver_driving_license_number varchar(20) not null,
	driver_email_address nvarchar(50) not null,
	driver_phone_number varchar(50) not null,
	registration_branch_id int not null
		constraint drivers_branches_branch_id_fk
			references branches
				on update cascade,
	created datetime default getdate() not null,
	updated datetime default getdate() not null
)
go

create unique index drivers_driver_national_id_number_uindex
	on drivers (driver_national_id_number)
go

create unique index drivers_driver_driving_license_number_uindex
	on drivers (driver_driving_license_number)
go

create unique index drivers_driver_email_address_uindex
	on drivers (driver_email_address)
go

create unique index drivers_driver_phone_number_uindex
	on drivers (driver_phone_number)
go

create table routes
(
	route_id int identity
		constraint routes_pk
			primary key nonclustered,
	route_name nvarchar(150) not null,
	route_description nvarchar(255),
	origin_branch_id int not null
		constraint routes_branches_branch_id_origin_branch_id
			references branches,
	destination_branch_id int not null
		constraint routes_branches_branch_id_destination_branch_id
			references branches,
	created datetime default getdate() not null,
	updated datetime default getdate() not null
)
go

create unique index routes_route_name_uindex
	on routes (route_name)
go

create table vehicle_types
(
	vehicle_type_id int identity
		constraint vehicle_types_pk
			primary key nonclustered,
	vehicle_type_name varchar(10) not null
		check ([vehicle_type_name]='BIKE' OR [vehicle_type_name]='SEDAN' OR [vehicle_type_name]='VAN' OR [vehicle_type_name]='BUS'),
	vehicle_type_model nvarchar(500) not null,
	created datetime default getdate() not null,
	updated datetime default getdate() not null
)
go

create table vehicles
(
	vehicle_id int identity
		constraint vehicles_pk
			primary key nonclustered,
	vehicle_type_id int not null
		constraint vehicles_vehicle_types_vehicle_type_id_fk
			references vehicle_types,
	registration_number varchar(50) not null,
	registration_branch_id int not null
		constraint vehicles_branches_branch_id_fk
			references branches
				on update cascade,
	created datetime default getdate() not null,
	updated datetime default getdate() not null
)
go

create table trips
(
	trip_id int identity
		constraint trips_pk
			primary key nonclustered,
	trip_name nvarchar(150) not null,
	trip_description nvarchar(1000),
	trip_driver_id int not null,
	trip_vehicle_id int not null
		constraint trips_vehicles_vehicle_id_fk
			references vehicles,
	trip_route_id int not null,
	trip_cost decimal(10,2) not null,
	trip_date date not null,
	trip_departure_time time not null,
	trip_eta time not null,
	trip_stops_branch_ids varchar(500),
	created datetime default getdate() not null,
	updated datetime default getdate() not null
)
go

create table passengers
(
	passenger_id int identity
		constraint passengers_pk
			primary key nonclustered,
	passenger_customer_id int not null
		constraint passengers_customers_customer_id_fk
			references customers,
	passenger_trip_id int not null
		constraint passengers_trips_trip_id_fk
			references trips,
	passenger_seat_number varchar(10) not null,
	passenger_paid varchar(5) not null
		check ([passenger_paid]='NO' OR [passenger_paid]='YES'),
	created datetime default getdate() not null,
	updated datetime default getdate() not null
)
go

create unique index passengers_passenger_trip_id_passenger_seat_number_uindex
	on passengers (passenger_trip_id, passenger_seat_number)
go

create unique index vehicles_registration_number_uindex
	on vehicles (registration_number)
go

-- insert data
-- if you're doing this manually, kindly do it in the order below
SET IDENTITY_INSERT TransyDB.dbo.branches  ON;
INSERT INTO TransyDB.dbo.branches (branch_id, branch_code, branch_name, branch_location, created, updated) VALUES (1, 'NBO', 'Nairobi', 'Nairobi is a city found in Nairobi Area, Kenya. It is located -1.28 latitude and 36.82 longitude and it is situated at elevation 1684 meters above sea level. Nairobi has a population of 2,750,547 making it the biggest city in Nairobi Area', '2019-05-31 03:26:25.437', '2019-05-31 03:26:25.437');
INSERT INTO TransyDB.dbo.branches (branch_id, branch_code, branch_name, branch_location, created, updated) VALUES (2, 'MSA', 'Mombasa', 'Mombasa is a city found in Mombasa Area, Kenya. It is located -1.28 latitude and 36.82 longitude and it is situated at elevation 200 meters above sea level. Nairobi has a population of 2,750,547 making it the second biggest city in Mombasa Area', '2019-05-31 03:28:12.080', '2019-05-31 03:28:12.080');
INSERT INTO TransyDB.dbo.branches (branch_id, branch_code, branch_name, branch_location, created, updated) VALUES (3, 'KSM', 'Kisumu', 'Kisumu is a city found in Kisumu Area, Kenya. It is located -1.28 latitude and 36.82 longitude and it is situated at elevation 900 meters above sea level. Nairobi has a population of 2,750,547 making it the third biggest city in Kisumu Area', '2019-05-31 03:28:59.813', '2019-05-31 03:28:59.813');
SET IDENTITY_INSERT TransyDB.dbo.branches  OFF;

SET IDENTITY_INSERT TransyDB.dbo.routes  ON;
INSERT INTO TransyDB.dbo.routes (route_id, route_name, route_description, origin_branch_id, destination_branch_id, created, updated) VALUES (1, 'NBO-MSA', 'Nairobi to Mombasa', 1, 2, '2019-06-10 15:26:11.197', '2019-06-10 15:26:11.197');
INSERT INTO TransyDB.dbo.routes (route_id, route_name, route_description, origin_branch_id, destination_branch_id, created, updated) VALUES (2, 'NBO-KSM', 'Nairobi to Kisumu', 1, 3, '2019-06-10 15:26:33.100', '2019-06-10 15:26:33.100');
INSERT INTO TransyDB.dbo.routes (route_id, route_name, route_description, origin_branch_id, destination_branch_id, created, updated) VALUES (3, 'NBO(CBD)-NBO(KAREN)', 'Nairobi (CBD) to Nairobi (Karen)', 1, 1, '2019-06-10 15:27:11.807', '2019-06-10 15:27:11.807');
INSERT INTO TransyDB.dbo.routes (route_id, route_name, route_description, origin_branch_id, destination_branch_id, created, updated) VALUES (4, 'NBO(CBD)-NBO(KILELESHWA)', 'Nairobi (CBD) to Nairobi (Kileleshwa)', 1, 1, '2019-06-10 15:27:48.400', '2019-06-10 15:27:48.400');
SET IDENTITY_INSERT TransyDB.dbo.routes  OFF;

SET IDENTITY_INSERT TransyDB.dbo.vehicle_types  ON;
INSERT INTO TransyDB.dbo.vehicle_types (vehicle_type_id, vehicle_type_name, vehicle_type_model, created, updated) VALUES (1, 'SEDAN', 'Mercedes Benz (S Class)', '2019-05-31 03:33:40.883', '2019-05-31 03:33:40.883');
INSERT INTO TransyDB.dbo.vehicle_types (vehicle_type_id, vehicle_type_name, vehicle_type_model, created, updated) VALUES (2, 'SEDAN', 'BMW M3', '2019-05-31 03:35:56.733', '2019-05-31 03:35:56.733');
INSERT INTO TransyDB.dbo.vehicle_types (vehicle_type_id, vehicle_type_name, vehicle_type_model, created, updated) VALUES (3, 'SEDAN', 'Audi A6', '2019-05-31 03:41:34.050', '2019-05-31 03:41:34.050');
INSERT INTO TransyDB.dbo.vehicle_types (vehicle_type_id, vehicle_type_name, vehicle_type_model, created, updated) VALUES (6, 'BUS', 'Isuzu Scania', '2019-05-31 03:44:07.310', '2019-05-31 03:44:07.310');
INSERT INTO TransyDB.dbo.vehicle_types (vehicle_type_id, vehicle_type_name, vehicle_type_model, created, updated) VALUES (5, 'VAN', 'Toyota Hiace', '2019-05-31 03:43:21.960', '2019-05-31 03:43:21.960');
INSERT INTO TransyDB.dbo.vehicle_types (vehicle_type_id, vehicle_type_name, vehicle_type_model, created, updated) VALUES (7, 'BUS', 'Volvo B8L', '2019-05-31 03:45:37.460', '2019-05-31 03:45:37.460');
SET IDENTITY_INSERT TransyDB.dbo.vehicle_types  OFF;

SET IDENTITY_INSERT TransyDB.dbo.vehicles  ON;
INSERT INTO TransyDB.dbo.vehicles (vehicle_id, vehicle_type_id, registration_number, registration_branch_id, created, updated) VALUES (1, 2, 'KCG 898X', 1, '2019-06-10 15:38:57.030', '2019-06-10 15:38:57.030');
INSERT INTO TransyDB.dbo.vehicles (vehicle_id, vehicle_type_id, registration_number, registration_branch_id, created, updated) VALUES (2, 1, 'KCS 890S', 2, '2019-06-10 15:39:14.663', '2019-06-10 15:39:14.663');
INSERT INTO TransyDB.dbo.vehicles (vehicle_id, vehicle_type_id, registration_number, registration_branch_id, created, updated) VALUES (3, 6, 'KCD 654Y', 3, '2019-06-10 15:39:30.350', '2019-06-10 15:39:30.350');
INSERT INTO TransyDB.dbo.vehicles (vehicle_id, vehicle_type_id, registration_number, registration_branch_id, created, updated) VALUES (4, 5, 'KDD 789D', 1, '2019-06-10 15:39:48.347', '2019-06-10 15:39:48.347');
INSERT INTO TransyDB.dbo.vehicles (vehicle_id, vehicle_type_id, registration_number, registration_branch_id, created, updated) VALUES (5, 3, 'KCT 880J', 2, '2019-06-10 15:40:10.273', '2019-06-10 15:40:10.273');
INSERT INTO TransyDB.dbo.vehicles (vehicle_id, vehicle_type_id, registration_number, registration_branch_id, created, updated) VALUES (7, 7, 'KJJ 894R', 3, '2019-06-10 15:41:00.423', '2019-06-10 15:41:00.423');
INSERT INTO TransyDB.dbo.vehicles (vehicle_id, vehicle_type_id, registration_number, registration_branch_id, created, updated) VALUES (8, 2, 'KCD 878X', 1, '2019-06-10 15:38:57.030', '2019-06-10 15:38:57.030');
INSERT INTO TransyDB.dbo.vehicles (vehicle_id, vehicle_type_id, registration_number, registration_branch_id, created, updated) VALUES (9, 1, 'KDS 894S', 2, '2019-06-10 15:39:14.663', '2019-06-10 15:39:14.663');
INSERT INTO TransyDB.dbo.vehicles (vehicle_id, vehicle_type_id, registration_number, registration_branch_id, created, updated) VALUES (10, 6, 'KCC 654Z', 3, '2019-06-10 15:39:30.350', '2019-06-10 15:39:30.350');
INSERT INTO TransyDB.dbo.vehicles (vehicle_id, vehicle_type_id, registration_number, registration_branch_id, created, updated) VALUES (11, 5, 'KBQ 789Z', 1, '2019-06-10 15:39:48.347', '2019-06-10 15:39:48.347');
INSERT INTO TransyDB.dbo.vehicles (vehicle_id, vehicle_type_id, registration_number, registration_branch_id, created, updated) VALUES (12, 3, 'KCE 780J', 2, '2019-06-10 15:40:10.273', '2019-06-10 15:40:10.273');
INSERT INTO TransyDB.dbo.vehicles (vehicle_id, vehicle_type_id, registration_number, registration_branch_id, created, updated) VALUES (13, 7, 'KBJ 894R', 3, '2019-06-10 15:41:00.423', '2019-06-10 15:41:00.423');
SET IDENTITY_INSERT TransyDB.dbo.vehicles  OFF;

SET IDENTITY_INSERT TransyDB.dbo.drivers  ON;
INSERT INTO TransyDB.dbo.drivers (driver_id, driver_first_name, driver_last_name, driver_other_names, driver_national_id_number, driver_driving_license_number, driver_email_address, driver_phone_number, registration_branch_id, created, updated) VALUES (1, 'William', 'Otieno', 'Ochomo', '33984458', 'C345HF8888', 'ochomoswill@gmail.com', '254739000948', 1, '2019-06-10 13:38:56.453', '2019-06-10 13:38:56.453');
INSERT INTO TransyDB.dbo.drivers (driver_id, driver_first_name, driver_last_name, driver_other_names, driver_national_id_number, driver_driving_license_number, driver_email_address, driver_phone_number, registration_branch_id, created, updated) VALUES (4, 'Innocent', 'Kithinji', null, '33498928', '478UI9800P', 'ikithinji@gmail.com', '254712333498', 1, '2019-06-10 13:42:11.273', '2019-06-10 13:42:11.273');
SET IDENTITY_INSERT TransyDB.dbo.drivers  OFF;

SET IDENTITY_INSERT TransyDB.dbo.trips  ON;
INSERT INTO TransyDB.dbo.trips (trip_id, trip_name, trip_description, trip_driver_id, trip_vehicle_id, trip_route_id, trip_cost, trip_date, trip_departure_time, trip_eta, trip_stops_branch_ids, created, updated) VALUES (1, 'Trip Oreo', 'Trip Oreo', 7, 13, 10, 3000.00, '2019-06-05', '08:00:00', '17:00:00', '1,2,3', '2019-06-10 15:50:41.970', '2019-06-10 15:50:41.970');
INSERT INTO TransyDB.dbo.trips (trip_id, trip_name, trip_description, trip_driver_id, trip_vehicle_id, trip_route_id, trip_cost, trip_date, trip_departure_time, trip_eta, trip_stops_branch_ids, created, updated) VALUES (2, 'Trip Zion Duplex', 'Trip Zion Duplex', 1, 9, 4, 2500.00, '2019-06-18', '08:00:00', '15:45:00', '1,2,3', '2019-06-10 15:51:40.253', '2019-06-10 15:51:40.253');
INSERT INTO TransyDB.dbo.trips (trip_id, trip_name, trip_description, trip_driver_id, trip_vehicle_id, trip_route_id, trip_cost, trip_date, trip_departure_time, trip_eta, trip_stops_branch_ids, created, updated) VALUES (3, 'Trip Xenophobia', 'Trip Xenophobia', 4, 5, 1, 50.00, '2019-06-18', '08:00:00', '08:45:00', '1', '2019-06-10 15:53:12.090', '2019-06-10 15:53:12.090');
INSERT INTO TransyDB.dbo.trips (trip_id, trip_name, trip_description, trip_driver_id, trip_vehicle_id, trip_route_id, trip_cost, trip_date, trip_departure_time, trip_eta, trip_stops_branch_ids, created, updated) VALUES (4, 'Trip XYZ', 'Trip XYZ', 5, 7, 3, 10000.00, '2019-06-27', '14:00:00', '09:00:00', '1,2,3', '2019-06-10 15:54:48.183', '2019-06-10 15:54:48.183');
INSERT INTO TransyDB.dbo.trips (trip_id, trip_name, trip_description, trip_driver_id, trip_vehicle_id, trip_route_id, trip_cost, trip_date, trip_departure_time, trip_eta, trip_stops_branch_ids, created, updated) VALUES (5, 'Trip Business Class', 'Trip Business Class', 6, 10, 7, 15000.00, '2019-06-24', '09:00:00', '11:00:00', '1,2,3,4,5', '2019-06-10 15:56:49.830', '2019-06-10 15:56:49.830');
INSERT INTO TransyDB.dbo.trips (trip_id, trip_name, trip_description, trip_driver_id, trip_vehicle_id, trip_route_id, trip_cost, trip_date, trip_departure_time, trip_eta, trip_stops_branch_ids, created, updated) VALUES (6, 'Trip Legion Y720', 'Trip Legion Y720', 1, 8, 5, 150.00, '2019-06-22', '08:00:00', '08:50:00', '1', '2019-06-10 15:59:08.310', '2019-06-10 15:59:08.310');
INSERT INTO TransyDB.dbo.trips (trip_id, trip_name, trip_description, trip_driver_id, trip_vehicle_id, trip_route_id, trip_cost, trip_date, trip_departure_time, trip_eta, trip_stops_branch_ids, created, updated) VALUES (8, 'Trip K501UX', 'Trip K501UX', 5, 12, 6, 3000.00, '2019-06-12', '07:00:00', '14:00:00', '1,2', '2019-06-10 16:01:05.057', '2019-06-10 16:01:05.057');
INSERT INTO TransyDB.dbo.trips (trip_id, trip_name, trip_description, trip_driver_id, trip_vehicle_id, trip_route_id, trip_cost, trip_date, trip_departure_time, trip_eta, trip_stops_branch_ids, created, updated) VALUES (9, 'Trip Shogun', 'Trip Shogun', 4, 11, 3, 4500.00, '2019-06-03', '09:00:00', '18:00:00', '1', '2019-06-10 16:02:27.643', '2019-06-10 16:02:27.643');
INSERT INTO TransyDB.dbo.trips (trip_id, trip_name, trip_description, trip_driver_id, trip_vehicle_id, trip_route_id, trip_cost, trip_date, trip_departure_time, trip_eta, trip_stops_branch_ids, created, updated) VALUES (10, 'Trip Avensis-Tx3', 'Trip Avensis-Tx3', 7, 8, 2, 800.00, '2019-06-30', '06:00:00', '08:00:00', '1', '2019-06-10 16:03:53.577', '2019-06-10 16:03:53.577');
INSERT INTO TransyDB.dbo.trips (trip_id, trip_name, trip_description, trip_driver_id, trip_vehicle_id, trip_route_id, trip_cost, trip_date, trip_departure_time, trip_eta, trip_stops_branch_ids, created, updated) VALUES (11, 'Trip RJ54', 'Trip RJ54', 6, 9, 8, 2500.00, '2019-06-18', '07:00:00', '15:45:00', '1,1', '2019-06-10 16:05:52.313', '2019-06-10 16:05:52.313');
SET IDENTITY_INSERT TransyDB.dbo.trips  OFF;

SET IDENTITY_INSERT TransyDB.dbo.customers ON;
INSERT INTO TransyDB.dbo.customers (customer_id, customer_first_name, customer_last_name, customer_other_names, customer_national_id_number, customer_email_address, customer_phone_number, created, updated) VALUES (1, 'Anne', 'Nandai', 'Ouma', '23499854', 'anney@gmail.com', '254723111984', '2019-06-10 15:33:58.357', '2019-06-10 15:33:58.357');
INSERT INTO TransyDB.dbo.customers (customer_id, customer_first_name, customer_last_name, customer_other_names, customer_national_id_number, customer_email_address, customer_phone_number, created, updated) VALUES (2, 'Emily', 'Mutheu', null, '23449892', 'emutheu@outlook.com', '254712333985', '2019-06-10 15:34:34.597', '2019-06-10 15:34:34.597');
INSERT INTO TransyDB.dbo.customers (customer_id, customer_first_name, customer_last_name, customer_other_names, customer_national_id_number, customer_email_address, customer_phone_number, created, updated) VALUES (3, 'Stacy', 'Nakoy', 'Simiyu', '33989928', 'nekoy97@gmail.com', '254723999092', '2019-06-10 15:35:09.973', '2019-06-10 15:35:09.973');
INSERT INTO TransyDB.dbo.customers (customer_id, customer_first_name, customer_last_name, customer_other_names, customer_national_id_number, customer_email_address, customer_phone_number, created, updated) VALUES (4, 'Patrick', 'Semedo', null, '223309895', 'semedop@gmail.com', '254723999095', '2019-06-10 15:35:35.403', '2019-06-10 15:35:35.403');
INSERT INTO TransyDB.dbo.customers (customer_id, customer_first_name, customer_last_name, customer_other_names, customer_national_id_number, customer_email_address, customer_phone_number, created, updated) VALUES (5, 'Vigil', 'Djik', 'van', '23998949', 'vdjik@gmail.com', '254789002930', '2019-06-10 15:36:13.787', '2019-06-10 15:36:13.787');
INSERT INTO TransyDB.dbo.customers (customer_id, customer_first_name, customer_last_name, customer_other_names, customer_national_id_number, customer_email_address, customer_phone_number, created, updated) VALUES (6, 'Memphis', 'Pay', 'de', '230900940', 'depay@outlook.com', '254788903990', '2019-06-10 15:36:47.723', '2019-06-10 15:36:47.723');
INSERT INTO TransyDB.dbo.customers (customer_id, customer_first_name, customer_last_name, customer_other_names, customer_national_id_number, customer_email_address, customer_phone_number, created, updated) VALUES (7, 'David', 'Gea', 'de', '321898749', 'degead@gmail.com', '254789333948', '2019-06-10 15:37:24.817', '2019-06-10 15:37:24.817');
INSERT INTO TransyDB.dbo.customers (customer_id, customer_first_name, customer_last_name, customer_other_names, customer_national_id_number, customer_email_address, customer_phone_number, created, updated) VALUES (8, 'Christiano', 'Ronaldo', null, '239904998', 'roro@gmail.com', '254723898949', '2019-06-10 15:37:51.687', '2019-06-10 15:37:51.687');
SET IDENTITY_INSERT TransyDB..customers OFF;

SET IDENTITY_INSERT TransyDB.dbo.passengers ON;
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (24, 1, 1, '2A', 'YES', '2019-06-10 16:12:12.833', '2019-06-10 16:12:12.833');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (25, 3, 2, '2B', 'YES', '2019-06-10 16:12:12.840', '2019-06-10 16:12:12.840');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (26, 4, 4, '7C', 'YES', '2019-06-10 16:12:12.843', '2019-06-10 16:12:12.843');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (27, 5, 5, '6B', 'YES', '2019-06-10 16:12:12.850', '2019-06-10 16:12:12.850');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (28, 6, 6, '4D', 'YES', '2019-06-10 16:12:12.853', '2019-06-10 16:12:12.853');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (29, 8, 5, '3D', 'YES', '2019-06-10 16:12:12.857', '2019-06-10 16:12:12.857');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (30, 7, 11, '2D', 'NO', '2019-06-10 16:12:12.860', '2019-06-10 16:12:12.860');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (31, 3, 8, '5C', 'YES', '2019-06-10 16:12:12.863', '2019-06-10 16:12:12.863');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (32, 3, 10, '9D', 'YES', '2019-06-10 16:12:12.870', '2019-06-10 16:12:12.870');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (33, 4, 2, '9E', 'YES', '2019-06-10 16:12:12.873', '2019-06-10 16:12:12.873');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (34, 5, 3, '8C', 'YES', '2019-06-10 16:12:12.877', '2019-06-10 16:12:12.877');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (35, 6, 5, '7D', 'YES', '2019-06-10 16:12:12.880', '2019-06-10 16:12:12.880');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (36, 7, 6, '3D', 'YES', '2019-06-10 16:12:12.887', '2019-06-10 16:12:12.887');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (37, 2, 10, '6A', 'NO', '2019-06-10 16:12:12.890', '2019-06-10 16:12:12.890');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (38, 3, 9, '7B', 'YES', '2019-06-10 16:12:12.893', '2019-06-10 16:12:12.893');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (39, 4, 6, '5D', 'YES', '2019-06-10 16:12:12.897', '2019-06-10 16:12:12.897');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (40, 1, 4, '9A', 'YES', '2019-06-10 16:12:12.900', '2019-06-10 16:12:12.900');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (41, 2, 4, '5C', 'YES', '2019-06-10 16:12:12.903', '2019-06-10 16:12:12.903');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (42, 4, 1, '8A', 'YES', '2019-06-10 16:12:12.910', '2019-06-10 16:12:12.910');
INSERT INTO TransyDB.dbo.passengers (passenger_id, passenger_customer_id, passenger_trip_id, passenger_seat_number, passenger_paid, created, updated) VALUES (43, 6, 2, '7C', 'YES', '2019-06-10 16:12:12.910', '2019-06-10 16:12:12.910');
SET IDENTITY_INSERT TransyDB.dbo.passengers OFF;