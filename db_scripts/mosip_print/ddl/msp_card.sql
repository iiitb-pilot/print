-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_print
-- Table Name 	: print.msp_card
-- Purpose    	: MSP card: MSP Card print table, Store all the printable data in card.
--
-- Create By   	: Murali
-- Created Date	: 02-Nov-2022
--
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------

-- ------------------------------------------------------------------------------------------

-- DROP TABLE IF EXISTS print.msp_card CASCADE;

CREATE TABLE IF NOT EXISTS print.msp_card (
	id varchar NOT NULL,
	json_data varchar NULL,
	province varchar(100) NULL,
	city varchar(100) NULL,
	'zone' varchar(100) NULL,
	zip varchar(100) NULL,
	agegroup int4 NULL,
	introducer varchar(100) NULL,
	resident varchar(100) NULL,
	registration_center_id varchar(100) NULL,
	registration_date timestamp NULL,
	download_date timestamptz NULL,
	request_id varchar NULL,
	status int4 NULL,
	request_id1 varchar NULL,
	birthdate date NULL,
	CONSTRAINT pk_prnt_id PRIMARY KEY (id)
);
