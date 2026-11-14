package com.carbonfootprint.ui;

import com.carbonfootprint.model.*;
import com.carbonfootprint.service.CalculoService;
import com.carbonfootprint.service.UsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Interface de linha de comando (Console).
 * Organiza os menus de usuários e cálculos, delegando a lógica
 * para as camadas de serviço.
 */
public class ConsoleUI {

    private final Scanner        sc  = new Scanner(System.in);
    private final UsuarioService us  = new UsuarioService();
    private final CalculoService cs  = new CalculoService();

    // ── Menu principal ────────────────────────────────────────────────────────
    public void iniciar() {
        System.out.println("\n🌱 Calculadora de Pegada de Carbono — Java + PostgreSQL");
        boolean rodando = true;
        while (rodando) {
            System.out.println("""

                ╔══════════════════════════════╗
                ║          MENU PRINCIPAL       ║
                ╠══════════════════════════════╣
                ║  1. Gerenciar Usuários        ║
                ║  2. Gerenciar Cálculos        ║
                ║  0. Sair                      ║
                ╚══════════════════════════════╝
                Opção: """);
            switch (ler()) {
                case "1" -> menuUsuarios();
                case "2" -> menuCalculos();
                case "0" -> rodando = false;
                default  -> System.out.println("❌ Opção inválida.");
            }
        }
        System.out.println("👋 Até logo!");
    }

    // ── Menu de usuários (CRUD) ───────────────────────────────────────────────
    private void menuUsuarios() {
        boolean sub = true;
        while (sub) {
            System.out.println("""

                ── USUÁRIOS ──────────────────────
                  1. Cadastrar novo usuário
                  2. Listar todos os usuários
                  3. Buscar usuário por ID
                  4. Atualizar usuário
                  5. Remover usuário
                  0. Voltar
                Opção: """);
            switch (ler()) {
                case "1" -> cadastrarUsuario();
                case "2" -> listarUsuarios();
                case "3" -> buscarUsuarioPorId();
                case "4" -> atualizarUsuario();
                case "5" -> removerUsuario();
                case "0" -> sub = false;
                default  -> System.out.println("❌ Opção inválida.");
            }
        }
    }

    private void cadastrarUsuario() {
        System.out.print("Nome  : "); String nome   = ler();
        System.out.print("E-mail: "); String email  = ler();
        System.out.print("Cidade: "); String cidade = ler();
        try {
            Usuario u = us.cadastrar(nome, email, cidade);
            System.out.println("✅ Usuário cadastrado: ID=" + u.getId());
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    private void listarUsuarios() {
        List<Usuario> lista = us.listarTodos();
        if (lista.isEmpty()) { System.out.println("Nenhum usuário cadastrado."); return; }
        System.out.println("\n── Usuários ──────────────────────────────────────────");
        lista.forEach(u -> System.out.printf("  ID=%-4d %-20s %-30s %s%n",
                u.getId(), u.getNome(), u.getEmail(), u.getCidade()));
    }

    private void buscarUsuarioPorId() {
        System.out.print("ID do usuário: ");
        int id = lerInt();
        us.buscarPorId(id).ifPresentOrElse(
            u -> System.out.println("  " + u),
            ()-> System.out.println("❌ Usuário não encontrado.")
        );
    }

    private void atualizarUsuario() {
        System.out.print("ID do usuário a atualizar: ");
        int id = lerInt();
        Optional<Usuario> opt = us.buscarPorId(id);
        if (opt.isEmpty()) { System.out.println("❌ Usuário não encontrado."); return; }
        Usuario u = opt.get();
        System.out.print("Novo nome  [" + u.getNome()   + "]: "); String nome   = ler();
        System.out.print("Novo e-mail[" + u.getEmail()  + "]: "); String email  = ler();
        System.out.print("Nova cidade[" + u.getCidade() + "]: "); String cidade = ler();
        if (!nome.isBlank())   u.setNome(nome);
        if (!email.isBlank())  u.setEmail(email);
        if (!cidade.isBlank()) u.setCidade(cidade);
        try {
            boolean ok = us.atualizar(u);
            System.out.println(ok ? "✅ Atualizado." : "❌ Falha na atualização.");
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
    }

    private void removerUsuario() {
        System.out.print("ID do usuário a remover: ");
        int id = lerInt();
        System.out.print("Confirmar remoção? (s/n): ");
        if ("s".equalsIgnoreCase(ler())) {
            boolean ok = us.deletar(id);
            System.out.println(ok ? "✅ Usuário removido." : "❌ Usuário não encontrado.");
        }
    }

    // ── Menu de cálculos (CRUD) ───────────────────────────────────────────────
    private void menuCalculos() {
        boolean sub = true;
        while (sub) {
            System.out.println("""

                ── CÁLCULOS ──────────────────────
                  1. Novo cálculo de pegada
                  2. Listar todos os cálculos
                  3. Buscar cálculo por ID
                  4. Ver histórico de um usuário
                  5. Remover cálculo
                  0. Voltar
                Opção: """);
            switch (ler()) {
                case "1" -> novoCalculo();
                case "2" -> listarCalculos();
                case "3" -> buscarCalculoPorId();
                case "4" -> historicoPorUsuario();
                case "5" -> removerCalculo();
                case "0" -> sub = false;
                default  -> System.out.println("❌ Opção inválida.");
            }
        }
    }

    private void novoCalculo() {
        System.out.print("ID do usuário: ");
        int id = lerInt();
        Optional<Usuario> opt = us.buscarPorId(id);
        if (opt.isEmpty()) { System.out.println("❌ Usuário não encontrado."); return; }
        Usuario u = opt.get();

        System.out.print("KM percorridos por semana: ");
        double km = lerDouble();

        System.out.println("Transporte (Carro / Moto / Ônibus / Metrô / Bicicleta / Pé): ");
        String transp = lerOpcao(new String[]{"Carro", "Moto", "Ônibus / Metrô", "Bicicleta / Pé"});

        System.out.println("Energia (Baixo / Médio / Alto): ");
        String energia = lerOpcao(new String[]{"Baixo", "Médio", "Alto"});

        System.out.println("Alimentação (Muita carne / Moderado / Vegetariano): ");
        String alim = lerOpcao(new String[]{"Muita carne", "Moderado", "Vegetariano"});

        System.out.println("Consumo/Compras (Frequente / Moderado / Raramente): ");
        String consumo = lerOpcao(new String[]{"Frequente", "Moderado", "Raramente"});

        System.out.println("Resíduos (Sem reciclagem / Alguma reciclagem / Reciclagem ativa): ");
        String residuos = lerOpcao(new String[]{"Sem reciclagem", "Alguma reciclagem", "Reciclagem ativa"});

        CalculoResult resultado = cs.calcularEPersistir(u, km, transp, energia, alim, consumo, residuos);
        System.out.println(resultado.gerarRelatorioCompleto());
    }

    private void listarCalculos() {
        List<CalculoResult> lista = cs.listarTodos();
        if (lista.isEmpty()) { System.out.println("Nenhum cálculo registrado."); return; }
        System.out.println("\n── Cálculos ──────────────────────────────────────────");
        lista.forEach(c -> System.out.printf(
            "  ID=%-4d Usuário=%-20s Total=%-8.2f kg  %s  %s%n",
            c.getId(), c.getUsuario().getNome(), c.getTotalEmissao(),
            c.getClassificacao(), c.getDataCalculo()));
    }

    private void buscarCalculoPorId() {
        System.out.print("ID do cálculo: ");
        int id = lerInt();
        cs.buscarPorId(id).ifPresentOrElse(
            c -> System.out.println(c.gerarRelatorioCompleto()),
            () -> System.out.println("❌ Cálculo não encontrado.")
        );
    }

    private void historicoPorUsuario() {
        System.out.print("ID do usuário: ");
        int id = lerInt();
        List<CalculoResult> lista = cs.listarPorUsuario(id);
        if (lista.isEmpty()) { System.out.println("Nenhum cálculo para este usuário."); return; }
        lista.forEach(c -> System.out.println(c.gerarRelatorioCompleto()));
    }

    private void removerCalculo() {
        System.out.print("ID do cálculo a remover: ");
        int id = lerInt();
        System.out.print("Confirmar? (s/n): ");
        if ("s".equalsIgnoreCase(ler())) {
            boolean ok = cs.deletar(id);
            System.out.println(ok ? "✅ Cálculo removido." : "❌ Cálculo não encontrado.");
        }
    }

    // ── Auxiliares de entrada ─────────────────────────────────────────────────
    private String ler() { return sc.nextLine().trim(); }

    private int lerInt() {
        try   { int v = Integer.parseInt(ler()); return v; }
        catch (NumberFormatException e) { System.out.println("Valor inválido, usando 0."); return 0; }
    }

    private double lerDouble() {
        try   { return Double.parseDouble(ler()); }
        catch (NumberFormatException e) { System.out.println("Valor inválido, usando 0."); return 0; }
    }

    private String lerOpcao(String[] opcoes) {
        for (int i = 0; i < opcoes.length; i++)
            System.out.printf("  %d. %s%n", i + 1, opcoes[i]);
        System.out.print("Escolha (número): ");
        int idx = lerInt() - 1;
        if (idx < 0 || idx >= opcoes.length) {
            System.out.println("Opção inválida, usando a primeira.");
            return opcoes[0];
        }
        return opcoes[idx];
    }
}
