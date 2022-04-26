CREATE TABLE tb_parametro(
    id INTEGER unsigned auto_increment PRIMARY KEY NOT NULL,
    description VARCHAR(100) NOT NULL,
    vl_parameter DECIMAL NOT NULL);

CREATE index idx_emprestimo ON tb_parametro (description);

INSERT INTO tb_parametro values (NULL, "TAX_PATTERN", "15");