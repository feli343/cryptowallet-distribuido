CREATE DATABASE IF NOT EXISTS cryptowallet_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE cryptowallet_db;

DROP TABLE IF EXISTS transacoes;
DROP TABLE IF EXISTS carteiras;

CREATE TABLE carteiras (
    hash VARCHAR(90) PRIMARY KEY,
    apelido VARCHAR(80) NOT NULL,
    criada_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transacoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hash VARCHAR(90) NOT NULL,
    tipo ENUM('ENTRADA', 'SAIDA') NOT NULL,
    valor_btc DECIMAL(18, 8) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    data_movimentacao DATETIME NOT NULL,
    CONSTRAINT fk_transacoes_carteiras
        FOREIGN KEY (hash) REFERENCES carteiras(hash)
        ON DELETE CASCADE
);

INSERT INTO carteiras (hash, apelido) VALUES
('1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa', 'Carteira Genesis'),
('3J98t1WpEZ73CNmQviecrnyiWrnqRhWNLy', 'Carteira Cliente 02'),
('bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kygt080', 'Carteira Cliente 03');

INSERT INTO transacoes (hash, tipo, valor_btc, descricao, data_movimentacao) VALUES
('1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa', 'ENTRADA', 50.00000000, 'Primeira entrada registrada', '2026-05-20 09:30:00'),
('1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa', 'SAIDA',   5.00000000, 'Transferência para exchange', '2026-05-22 16:15:00'),
('1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa', 'SAIDA',   5.00000000, 'Pagamento de serviço', '2026-05-25 11:05:00'),
('1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa', 'SAIDA',   0.75000000, 'Taxa operacional', '2026-05-28 18:20:00'),
('1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa', 'ENTRADA', 1.50000000, 'Ajuste de saldo recebido', '2026-06-01 13:40:00'),

('3J98t1WpEZ73CNmQviecrnyiWrnqRhWNLy', 'ENTRADA', 2.35000000, 'Depósito inicial do cliente', '2026-05-18 10:00:00'),
('3J98t1WpEZ73CNmQviecrnyiWrnqRhWNLy', 'SAIDA',   0.20000000, 'Compra com criptoativo', '2026-05-23 12:10:00'),
('3J98t1WpEZ73CNmQviecrnyiWrnqRhWNLy', 'ENTRADA', 0.80000000, 'Recebimento externo', '2026-05-30 17:25:00'),

('bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kygt080', 'ENTRADA', 0.95000000, 'Depósito via rede BTC', '2026-05-19 08:45:00'),
('bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kygt080', 'SAIDA',   0.10000000, 'Saque solicitado', '2026-05-29 14:35:00');

SELECT
    hash,
    SUM(CASE WHEN tipo = 'ENTRADA' THEN valor_btc ELSE -valor_btc END) AS saldo_total_btc
FROM transacoes
GROUP BY hash;
