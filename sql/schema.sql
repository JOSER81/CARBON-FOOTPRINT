-- ============================================================
-- Calculadora de Pegada de Carbono — Schema PostgreSQL
-- (a aplicação também cria isso sozinha ao iniciar)
-- ============================================================
-- Para criar o banco (rodar como superusuário, ex.: psql -U postgres):
--   CREATE DATABASE carbon_footprint;
-- Depois conecte nele (\c carbon_footprint) e rode este arquivo.

CREATE TABLE IF NOT EXISTS usuarios (
    id            SERIAL       PRIMARY KEY,
    nome          VARCHAR(120) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    cidade        VARCHAR(100) NOT NULL,
    data_cadastro DATE         NOT NULL
);

CREATE TABLE IF NOT EXISTS calculos (
    id              SERIAL           PRIMARY KEY,
    usuario_id      INTEGER          NOT NULL,
    total_emissao   DOUBLE PRECISION NOT NULL,
    classificacao   VARCHAR(30)      NOT NULL,
    dica            TEXT             NOT NULL,
    data_calculo    TIMESTAMP        NOT NULL,
    CONSTRAINT fk_calculo_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS calculo_detalhes (
    id          SERIAL           PRIMARY KEY,
    calculo_id  INTEGER          NOT NULL,
    categoria   VARCHAR(50)      NOT NULL,
    descricao   VARCHAR(200)     NOT NULL,
    valor_kg    DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_detalhe_calculo
        FOREIGN KEY (calculo_id) REFERENCES calculos(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_calculos_usuario ON calculos(usuario_id);
CREATE INDEX IF NOT EXISTS idx_detalhes_calculo ON calculo_detalhes(calculo_id);
