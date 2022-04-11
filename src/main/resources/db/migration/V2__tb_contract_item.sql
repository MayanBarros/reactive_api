CREATE TABLE tb_contract_item (
	id MEDIUMINT AUTO_INCREMENT NOT NULL,
	contract_id MEDIUMINT,
	number_duplicata BIGINT,
	cash_value BIGINT,
	FOREIGN KEY (contract_id) REFERENCES tb_contract(id),
	PRIMARY KEY (id)
);



