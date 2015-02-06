
-- used in tests that use MYSQL

DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details` (
	`client_id` varchar(256) NOT NULL,
	`resource_ids` varchar(256) DEFAULT NULL,
	`client_secret` varchar(256) DEFAULT NULL,
	`scope` varchar(256) DEFAULT NULL,
	`authorized_grant_types` varchar(256) DEFAULT NULL,
	`web_server_redirect_uri` varchar(256) DEFAULT NULL,
	`authorities` varchar(256) DEFAULT NULL,
	`access_token_validity` int(11) DEFAULT NULL,
	`refresh_token_validity` int(11) DEFAULT NULL,
	`additional_information` varchar(4096) DEFAULT NULL,
	`autoapprove` varchar(256) DEFAULT NULL,
	PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `oauth_client_token`;
CREATE TABLE `oauth_client_token` (
	`token_id` varchar(256) DEFAULT NULL,
	`token` blob,
	`authentication_id` varchar(256) NOT NULL,
	`user_name` varchar(256) DEFAULT NULL,
	`client_id` varchar(256) DEFAULT NULL,
	PRIMARY KEY (`authentication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `oauth_access_token`;
CREATE TABLE `oauth_access_token` (
	`token_id` varchar(256) DEFAULT NULL,
	`token` blob,
	`authentication_id` varchar(256) NOT NULL,
	`user_name` varchar(256) DEFAULT NULL,
	`client_id` varchar(256) DEFAULT NULL,
	`authentication` blob,
	`refresh_token` varchar(256) DEFAULT NULL,
	PRIMARY KEY (`authentication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `oauth_refresh_token`;
CREATE TABLE `oauth_refresh_token` (
	`token_id` varchar(256) DEFAULT NULL,
	`token` blob,
	`authentication` blob
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `oauth_code`;
CREATE TABLE `oauth_code` (
	`code` varchar(256) DEFAULT NULL,
	`authentication` blob
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `oauth_approvals`;
CREATE TABLE `oauth_approvals` (
	`userId` varchar(256) DEFAULT NULL,
	`clientId` varchar(256) DEFAULT NULL,
	`scope` varchar(256) DEFAULT NULL,
	`status` varchar(10) DEFAULT NULL,
	`expiresAt` TIMESTAMP,
	`lastModifiedAt` TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- user credentials tables

DROP TABLE IF EXISTS `authority`;
CREATE TABLE `authority` (
  `id`  INT,
  `authority` varchar(255),
  primary key (id)
);

DROP TABLE IF EXISTS `credentials`;
CREATE TABLE `credentials` (
  `id`  INT,
  `enabled` TINYINT not null,
  `name` varchar(255) not null,
  `password` varchar(255) not null,
  `version` INT,
  primary key (id)
);

DROP TABLE IF EXISTS `credentials_authorities`;
CREATE TABLE `credentials_authorities` (
  `credentials_id` INT not null,
  `authorities_id` INT not null
);


INSERT INTO `authority` VALUES(0,'ROLE_OAUTH_ADMIN');
INSERT INTO `authority` VALUES(1,'ROLE_ADMIN');
INSERT INTO `authority` VALUES(2,'ROLE_USER');
INSERT INTO `credentials` VALUES(0,1,'oauth_admin','admin',0);
INSERT INTO `credentials` VALUES(1,1,'resource_admin','admin',0);
INSERT INTO `credentials` VALUES(2,1,'user','user',0);
INSERT INTO `credentials_authorities` VALUES(0,0);
INSERT INTO `credentials_authorities` VALUES(1,1);
INSERT INTO `credentials_authorities` VALUES(2,2);
