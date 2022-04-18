ALTER TABLE tb_contract
ADD contract_expiration DATE NOT NULL;

ALTER TABLE tb_contract_item
ADD duplicata_expiration DATE NOT NULL;