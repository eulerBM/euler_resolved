package org.erbr.db;

import java.sql.*;

public class SQLiteConnection {
    private static final String URL = "jdbc:sqlite:keys-ias.db"; // O arquivo do banco será criado no diretório do projeto

    // Cria uma conexão com o banco de dados SQLite
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Cria a tabela de configurações (se não existir)
    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS config (" +
                "id INTEGER PRIMARY KEY, " +
                "apiKeyGPT TEXT, " +
                "apiKeyDeepSeek TEXT, " +
                "apiKeyGemini TEXT, " +
                "apiKeyGrok TEXT);";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela: " + e.getMessage());
        }
    }

    // Insere ou atualiza as chaves de API
    public static void saveApiKeys(String apiKeyGPT, String apiKeyDeepSeek, String apiKeyGemini, String apiKeyGrok) {
        String sql = "INSERT INTO config (id, apiKeyGPT, apiKeyDeepSeek, apiKeyGemini, apiKeyGrok) " +
                "VALUES (1, ?, ?, ?, ?) " +
                "ON CONFLICT(id) DO UPDATE SET " +
                "apiKeyGPT = excluded.apiKeyGPT, " +
                "apiKeyDeepSeek = excluded.apiKeyDeepSeek, " +
                "apiKeyGemini = excluded.apiKeyGemini, " +
                "apiKeyGrok = excluded.apiKeyGrok";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, apiKeyGPT);
            stmt.setString(2, apiKeyDeepSeek);
            stmt.setString(3, apiKeyGemini);
            stmt.setString(4, apiKeyGrok);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao salvar as chaves da API: " + e.getMessage());
        }
    }

    // Recupera todas as chaves de API do banco de dados
    public static String[] getApiKeys() {
        String sql = "SELECT apiKeyGPT, apiKeyDeepSeek, apiKeyGemini, apiKeyGrok FROM config WHERE id = 1";
        String[] apiKeys = new String[4];

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                apiKeys[0] = rs.getString("apiKeyGPT");
                apiKeys[1] = rs.getString("apiKeyDeepSeek");
                apiKeys[2] = rs.getString("apiKeyGemini");
                apiKeys[3] = rs.getString("apiKeyGrok");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao recuperar as chaves da API: " + e.getMessage());
        }

        return apiKeys;
    }

}
