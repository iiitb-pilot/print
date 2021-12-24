-- object: regprcuser | type: ROLE --
-- DROP ROLE IF EXISTS regprcuser;
CREATE ROLE mspprintuser WITH
	INHERIT
	LOGIN
	PASSWORD :dbuserpwd;
-- ddl-end --
