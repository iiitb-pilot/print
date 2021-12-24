\c mosip_print sysadmin

-- object: grant_b0ae4f0dce | type: PERMISSION --
GRANT CREATE,CONNECT,TEMPORARY
   ON DATABASE mosip_print
   TO sysadmin;
-- ddl-end --

-- object: grant_99dd1cb062 | type: PERMISSION --
GRANT CREATE,CONNECT,TEMPORARY
   ON DATABASE mosip_print
   TO appadmin;
-- ddl-end --

-- object: grant_18180691b7 | type: PERMISSION --
GRANT CONNECT
   ON DATABASE mosip_print
   TO mspprintuser;
-- ddl-end --

-- object: grant_3543fb6cf7 | type: PERMISSION --
GRANT CREATE,USAGE
   ON SCHEMA print
   TO sysadmin;
-- ddl-end --

-- object: grant_3543fb6cf7 | type: PERMISSION --
GRANT USAGE
   ON SCHEMA print
   TO mspprintuser;
-- ddl-end --

-- object: grant_8e1a2559ed | type: PERMISSION --
GRANT SELECT,UPDATE
   ON ALL TABLES IN SCHEMA print
   TO mspprintuser;
-- ddl-end --

ALTER DEFAULT PRIVILEGES IN SCHEMA print
	GRANT SELECT,UPDATE ON TABLES TO mspprintuser;

-- object: grant_78ed2da4ee | type: PERMISSION --
GRANT SELECT,INSERT,UPDATE,DELETE,TRUNCATE,REFERENCES
   ON ALL TABLES IN SCHEMA print
   TO appadmin;
-- ddl-end --
