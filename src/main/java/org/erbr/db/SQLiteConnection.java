package org.erbr.db;

import java.sql.*;

public class SQLiteConnection {
    private static final String URL = "jdbc:sqlite:keys-ias.db";

    // Cria uma conexão com o banco de dados SQLite
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Cria a tabela de configurações (se não existir)
    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS config (" +
                "id INTEGER PRIMARY KEY, " +
                "apiKeyGPT TEXT);";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela: " + e.getMessage());
        }
    }

    // Insere ou atualiza a chave da API do GPT
    public static void saveApiKeyGPT(String apiKeyGPT) {
        String sql = "INSERT INTO config (id, apiKeyGPT) " +
                "VALUES (1, ?) " +
                "ON CONFLICT(id) DO UPDATE SET " +
                "apiKeyGPT = excluded.apiKeyGPT";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, apiKeyGPT);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao salvar a chave da API GPT: " + e.getMessage());
        }
    }

    // Recupera a chave da API do GPT do banco de dados
    public static String getApiKeyGPT() {
        String sql = "SELECT apiKeyGPT FROM config WHERE id = 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("apiKeyGPT");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao recuperar a chave da API GPT: " + e.getMessage());
        }

        return null;
    }
}
