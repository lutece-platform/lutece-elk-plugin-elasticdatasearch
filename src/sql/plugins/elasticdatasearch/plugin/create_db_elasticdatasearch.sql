
--
-- Structure for table elasticdatasearch_fieldsearch
--

DROP TABLE IF EXISTS elasticdatasearch_fieldsearch;
CREATE TABLE elasticdatasearch_fieldsearch (
id_fieldsearch int AUTO_INCREMENT,
name varchar(255) default '' NOT NULL,
mandatory SMALLINT,
PRIMARY KEY (id_fieldsearch)
);
