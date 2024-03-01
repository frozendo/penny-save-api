CREATE TABLE currency (
  id_currency int PRIMARY KEY NOT NULL,
  cd_currency varchar(3) NOT NULL,
  nm_currency varchar(25) NOT NULL,
  nr_symbol char(2) NOT NULL
);

CREATE TABLE financial_space (
  id_financial_space serial PRIMARY KEY NOT NULL,
  cd_external varchar(32) NOT NULL,
  nm_financial_space varchar(50) NOT NULL,
  in_active char(1) NOT NULL DEFAULT 'Y',
  dt_created timestamp NOT NULL,
  dt_updated timestamp,
  id_currency int NOT NULL
);

CREATE TABLE person (
  id_person serial PRIMARY KEY NOT NULL,
  cd_external varchar(32) NOT NULL,
  ds_email varchar(30) NOT NULL,
  nm_person varchar(50) NOT NULL,
  dt_birth timestamp NOT NULL,
  in_status char(1) NOT NULL DEFAULT 'P',
  ds_password varchar(72) NOT NULL,
  in_email_confirmed char(1) NOT NULL DEFAULT 'N',
  dt_created timestamp NOT NULL,
  dt_updated timestamp
);

CREATE TABLE person_financial_space (
  id_person_financial_space serial PRIMARY KEY NOT NULL,
  in_owner char(1) NOT NULL DEFAULT 'Y',
  in_principal char(1) NOT NULL DEFAULT 'N',
  dt_shared timestamp NOT NULL,
  in_status char(1) NOT NULL DEFAULT 'U',
  id_person bigint NOT NULL,
  id_financial_space bigint NOT NULL
);

CREATE UNIQUE INDEX uk_currency_name ON currency (nm_currency);

CREATE UNIQUE INDEX uk_space_external ON financial_space (cd_external);

CREATE UNIQUE INDEX uk_person_external ON person (cd_external);
CREATE INDEX idx_person_name ON person (ds_email);

CREATE UNIQUE INDEX uk_person_financial_space ON person_financial_space (id_person, id_financial_space);

ALTER TABLE financial_space ADD CONSTRAINT fk_financial_space_currency FOREIGN KEY (id_currency) REFERENCES currency (id_currency);
ALTER TABLE financial_space ADD CONSTRAINT ck_financial_space_active CHECK (in_active IN ('Y', 'N'));

ALTER TABLE person ADD CONSTRAINT ck_person_status CHECK (in_status IN ('P', 'A', 'B'));
ALTER TABLE person ADD CONSTRAINT ck_email_confirmation CHECK (in_email_confirmed IN ('Y', 'N'));

ALTER TABLE person_financial_space ADD CONSTRAINT fk_person_financial_space FOREIGN KEY (id_person) REFERENCES person (id_person);
ALTER TABLE person_financial_space ADD CONSTRAINT fk_financial_space_person FOREIGN KEY (id_financial_space) REFERENCES financial_space (id_financial_space);
ALTER TABLE person_financial_space ADD CONSTRAINT ck_person_owner CHECK (in_owner IN ('Y', 'N'));
ALTER TABLE person_financial_space ADD CONSTRAINT ck_financial_space_principal CHECK (in_principal IN ('Y', 'N'));
ALTER TABLE person_financial_space ADD CONSTRAINT ck_shared_status CHECK (in_status IN ('S', 'U'));

insert into currency (id_currency, cd_currency, nm_currency, nr_symbol) values (1, 'BRL', 'Real', 'R$');
insert into currency (id_currency, cd_currency, nm_currency, nr_symbol) values (2, 'USD', 'Dólar', '$');
insert into currency (id_currency, cd_currency, nm_currency, nr_symbol) values (3, 'EUR', 'Euro', '€');