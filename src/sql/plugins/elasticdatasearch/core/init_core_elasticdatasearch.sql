
--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'ELASTICDATASEARCH_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('ELASTICDATASEARCH_MANAGEMENT','elasticdatasearch.adminFeature.ManageElasticdataSearch.name',1,'jsp/admin/plugins/elasticdatasearch/ManageFieldsearchs.jsp','elasticdatasearch.adminFeature.ManageElasticdataSearch.description',0,'elasticdatasearch',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'ELASTICDATASEARCH_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('ELASTICDATASEARCH_MANAGEMENT',1);

