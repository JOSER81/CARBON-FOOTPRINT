package com.carbonfootprint.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * SINGLETON — garante uma única conexão ativa com o banco PostgreSQL.
 */
public class DatabaseConnection {

    // ── Configuração PostgreSQL (ajuste usuário e senha) ──────────────────────
    private static final String URL      = "jdbc:postgresql://localhost:5432/carbon_footprint";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "15AGO";

    private static DatabaseConnection instance;
    private        Connection          connection;

    private DatabaseConnection() {
        try {
            // Carrega driver PostgreSQL
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Banco de dados conectado: " + URL);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC não encontrado. Recarregue o projeto Maven (pom.xml).", e);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }

    /** Retorna a instância única (SINGLETON). */
    public static DatabaseConnection getInstance() {
        if (instance == null || !instance.isAlive()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private boolean isAlive() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void fechar() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Conexão encerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}