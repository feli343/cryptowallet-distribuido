package com.cryptospace.datacore;

import com.cryptospace.shared.CarteiraResponse;
import com.cryptospace.shared.TransacaoResponse;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarteiraRepository {
    private final DatabaseConfig config;

    public CarteiraRepository(DatabaseConfig config) {
        this.config = config;
    }

    public CarteiraResponse buscarPorHash(String hash) throws SQLException {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword())) {
            BigDecimal saldo = buscarSaldo(connection, hash);
            List<TransacaoResponse> transacoes = buscarUltimasTransacoes(connection, hash);
            return new CarteiraResponse(hash, saldo, transacoes);
        }
    }

    private BigDecimal buscarSaldo(Connection connection, String hash) throws SQLException {
        String sql = """
                SELECT COALESCE(
                    SUM(CASE WHEN tipo = 'ENTRADA' THEN valor_btc ELSE -valor_btc END),
                    0
                ) AS saldo_total_btc
                FROM transacoes
                WHERE hash = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, hash);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("saldo_total_btc");
                }
            }
        }

        return BigDecimal.ZERO;
    }

    private List<TransacaoResponse> buscarUltimasTransacoes(Connection connection, String hash) throws SQLException {
        String sql = """
                SELECT id, tipo, valor_btc, descricao,
                       DATE_FORMAT(data_movimentacao, '%Y-%m-%d %H:%i:%s') AS data_movimentacao
                FROM transacoes
                WHERE hash = ?
                ORDER BY data_movimentacao DESC, id DESC
                LIMIT 10
                """;

        List<TransacaoResponse> transacoes = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, hash);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    transacoes.add(new TransacaoResponse(
                            resultSet.getLong("id"),
                            resultSet.getString("tipo"),
                            resultSet.getBigDecimal("valor_btc"),
                            resultSet.getString("descricao"),
                            resultSet.getString("data_movimentacao")
                    ));
                }
            }
        }

        return transacoes;
    }
}
