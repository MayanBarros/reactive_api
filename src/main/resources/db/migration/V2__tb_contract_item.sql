CREATE TABLE tb_contract_item (
	id MEDIUMINT AUTO_INCREMENT NOT NULL,
	contract_id MEDIUMINT NOT NULL,
	number_duplicata BIGINT NOT NULL,
	cash_value BIGINT NOT NULL,
	FOREIGN KEY (contract_id) REFERENCES tb_contract(id),
	PRIMARY KEY (id)
);



