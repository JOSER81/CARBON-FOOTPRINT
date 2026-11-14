package com.carbonfootprint.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Cria as tabelas no PostgreSQL caso ainda não existam.
 * Deve ser chamado uma vez na inicialização da aplicação.
 *
 * Diferenças em relação ao SQLite:
 *  - SERIAL no lugar de INTEGER PRIMARY KEY AUTOINCREMENT
 *  - DATE / TIMESTAMP reais no lugar de TEXT
 *  - DOUBLE PRECISION no lugar de REAL
 */
public class DatabaseInitializer {

    public static void inicializar() {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        String sqlUsuarios = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id            SERIAL       PRIMARY KEY,
                nome          VARCHAR(120) NOT NULL,
                email         VARCHAR(150) NOT NULL UNIQUE,
                cidade        VARCHAR(100) NOT NULL,
                data_cadastro DATE         NOT NULL
            )
            """;

        String sqlCalculos = """
            CREATE TABLE IF NOT EXISTS calculos (
                id              SERIAL           PRIMARY KEY,
                usuario_id      INTEGER          NOT NULL,
                total_emissao   DOUBLE PRECISION NOT NULL,
                classificacao   VARCHAR(30)      NOT NULL,
                dica            TEXT             NOT NULL,
                data_calculo    TIMESTAMP        NOT NULL,
                CONSTRAINT fk_calculo_usuario
                    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
            )
            """;

        String sqlDetalhes = """
            CREATE TABLE IF NOT EXISTS calculo_detalhes (
                id          SERIAL           PRIMARY KEY,
                calculo_id  INTEGER          NOT NULL,
                categoria   VARCHAR(50)      NOT NULL,
                descricao   VARCHAR(200)     NOT NULL,
                valor_kg    DOUBLE PRECISION NOT NULL,
                CONSTRAINT fk_detalhe_calculo
                    FOREIGN KEY (calculo_id) REFERENCES calculos(id) ON DELETE CASCADE
            )
            """;

        String idxCalculosUsuario = "CREATE INDEX IF NOT EXISTS idx_calculos_usuario ON calculos(usuario_id)";
        String idxDetalhesCalculo = "CREATE INDEX IF NOT EXISTS idx_detalhes_calculo ON calculo_detalhes(calculo_id)";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlCalculos);
            stmt.execute(sqlDetalhes);
            stmt.execute(idxCalculosUsuario);
            stmt.execute(idxDetalhesCalculo);
            System.out.println("✅ Tabelas verificadas/criadas com sucesso.");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabelas: " + e.getMessage(), e);
        }
    }
}
