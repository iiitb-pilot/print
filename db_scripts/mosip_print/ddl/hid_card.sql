-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_print
-- Table Name 	: print.hid_card
-- Purpose    	: HID card: HID Card print table, Store all the printable data in card.
--
-- Create By   	: Murali
-- Created Date	: 02-Nov-2022
--
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------

-- ------------------------------------------------------------------------------------------

-- DROP TABLE IF EXISTS print.hid_card CASCADE;

CREATE TABLE IF NOT EXISTS print.hid_card(  
	id			VARCHAR(36) NOT NULL,  
	fullname 	VARCHAR(100),  
	gender 		VARCHAR(6),  
	locality 	VARCHAR(30),  
	phone 		VARCHAR(20),  
	address 	VARCHAR(30),  
	city 		VARCHAR(30),  
	state 		VARCHAR(30), 
	postalcode 	VARCHAR(10), 
	dateofbirth VARCHAR(30),  
	email 		VARCHAR(30),  
	uin 		VARCHAR(30),  
	vid 		VARCHAR(30),  
	photo 		TEXT,  
	lang_code 	VARCHAR(3) NOT NULL,  
	status_code VARCHAR(20) NOT NULL,  
	cr_by 		VARCHAR(256) NOT NULL,  
	cr_dtimes 	TIMESTAMP NOT NULL,  
	upd_by 		VARCHAR(256),  
	upd_dtimes 	TIMESTAMP,  
	is_deleted 	BOOLEAN DEFAULT FALSE,  
	del_dtimes 	TIMESTAMP,  
	CONSTRAINT pk_crdprnt_id PRIMARY KEY (id, lang_code) 
);

