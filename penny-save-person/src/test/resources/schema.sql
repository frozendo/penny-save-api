CREATE TABLE IF NOT EXISTS app_role (
  cd_role bigint NOT NULL,
  nm_role varchar(30) NOT NULL,
  in_active character(1) NOT NULL DEFAULT 'Y',

  PRIMARY KEY (cd_role)
);
