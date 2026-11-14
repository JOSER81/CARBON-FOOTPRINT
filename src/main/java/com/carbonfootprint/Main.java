package com.carbonfootprint;

import com.carbonfootprint.database.DatabaseConnection;
import com.carbonfootprint.database.DatabaseInitializer;
import com.carbonfootprint.ui.ConsoleUI;

/**
 * Ponto de entrada da aplicação.
 *
 * Fluxo de inicialização:
 *   1. Conecta ao banco PostgreSQL (DatabaseConnection — Singleton)
 *   2. Crias as tabelas se não existirem (DatabaseInitializer)
 *   3. Exibe o menu interativo no console (ConsoleUI)
 *   4. Encerra a conexão ao sair
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=".repeat(54));
        System.out.println("   🌱  Calculadora de Pegada de Carbono — Java OO  ");
        System.out.println("=".repeat(54));

        // 1 + 2 — Banco de dados
        DatabaseInitializer.inicializar();

        // 3 — Interface de usuário
        new ConsoleUI().iniciar();

        // 4 — Encerrar conexão
        DatabaseConnection.getInstance().fechar();
    }
}
