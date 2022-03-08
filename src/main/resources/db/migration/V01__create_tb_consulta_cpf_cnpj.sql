CREATE TABLE consulta_cpf_cnpj (
	id MEDIUMINT AUTO_INCREMENT NOT NULL,
	cpf_cnpj VARCHAR(14) NOT NULL,
	result INT NOT NULL,
	PRIMARY KEY (id)
);

ALTER TABLE consulta_cpf_cnpj ADD create_date TIMESTAMP NOT NULL;

ALTER TABLE consulta_cpf_cnpj ADD update_date TIMESTAMP;

SELECT * FROM consulta_cpf_cnpj;



