create database cs221;
use cs221;
create table `indexNum`
(
	`term` varchar(100) not null,
	`docu` varchar(100) not null,
	`score` decimal(8,4) not null
);


create table `indexA_F`
(
	`term` varchar(100) not null,
	`docu` varchar(100) not null,
	`score` decimal(8,4) not null
);

create table `indexG_L`
(
	`term` varchar(100) not null,
	`docu` varchar(100) not null,
	`score` decimal(8,4) not null
);
create table `indexM_R`
(
	`term` varchar(100) not null,
	`docu` varchar(100) not null,
	`score` decimal(8,4) not null
);
create table `indexS_Z`
(
	`term` varchar(100) not null,
	`docu` varchar(100) not null,
	`score` decimal(8,4) not null
);



--test table
create table `index`
(
	`term` varchar(100) not null,
	`docu` varchar(100) not null,
	`score` decimal(8,4) not null
);