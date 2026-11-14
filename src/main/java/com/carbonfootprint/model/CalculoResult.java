package com.carbonfootprint.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * COMPOSIÇÃO
 * CalculoResult É COMPOSTO por um Usuario e uma lista de EmissaoFator.
 * Não herda — tem uma referência. Isso é composição ("tem um").
 *
 * Também persiste no banco de dados (tabela calculos + detalhes).
 */
public class CalculoResult {

    private int           id;
    private Usuario       usuario;          // COMPOSIÇÃO — tem um Usuario
    private List<EmissaoFator> fatores;    // COMPOSIÇÃO — tem vários fatores
    private double        totalEmissao;
    private String        classificacao;
    private String        dica;
    private LocalDateTime dataCalculo;

    // ── Construtor principal ──────────────────────────────────────────────────
    public CalculoResult(Usuario usuario) {
        this.usuario     = usuario;
        this.fatores     = new ArrayList<>();
        this.dataCalculo = LocalDateTime.now();
    }

    // ── Construtor de reconstrução (vindo do BD) ──────────────────────────────
    public CalculoResult(int id, Usuario usuario, double totalEmissao,
                         String classificacao, String dica, LocalDateTime dataCalculo) {
        this.id            = id;
        this.usuario       = usuario;
        this.totalEmissao  = totalEmissao;
        this.classificacao = classificacao;
        this.dica          = dica;
        this.dataCalculo   = dataCalculo;
        this.fatores       = new ArrayList<>();
    }

    /** Adiciona um fator e recalcula o total. */
    public void adicionarFator(EmissaoFator fator) {
        fatores.add(fator);
        recalcular();
    }

    /** Recalcula total e classifica automaticamente. */
    public void recalcular() {
        totalEmissao  = fatores.stream().mapToDouble(EmissaoFator::calcular).sum();
        classificacao = classificar(totalEmissao);
        dica          = gerarDica(totalEmissao);
    }

    // ── Classificação interna ─────────────────────────────────────────────────
    private String classificar(double total) {
        if (total <= 150) return "BAIXA 🌿";
        if (total <= 300) return "MÉDIA ⚠️";
        return "ALTA 🔴";
    }

    private String gerarDica(double total) {
        if (total <= 150)
            return "Excelente! Considere energia solar e produtos locais.";
        if (total <= 300)
            return "Priorize transporte coletivo e reduza carne algumas vezes/semana.";
        return "Migre para transporte público e adote dieta mais vegetal — pode reduzir até 60%.";
    }

    // ── Relatório formatado ───────────────────────────────────────────────────
    public String gerarRelatorioCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔══════════════════════════════════════════════════════╗\n");
        sb.append("║          RELATÓRIO DE PEGADA DE CARBONO              ║\n");
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Usuário  : %-40s║%n", usuario.getNome()));
        sb.append(String.format("║  Cidade   : %-40s║%n", usuario.getCidade()));
        sb.append(String.format("║  Data     : %-40s║%n", dataCalculo));
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        sb.append("║  BREAKDOWN POR CATEGORIA                             ║\n");
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        for (EmissaoFator f : fatores) {
            sb.append(String.format("║  %-52s║%n", f.gerarRelatorio()));
        }
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  TOTAL    : %-6.2f kg CO₂%-27s║%n", totalEmissao, ""));
        sb.append(String.format("║  NÍVEL    : %-40s║%n", classificacao));
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  DICA: %-46s║%n", dica));
        sb.append("╚══════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────
    public int                getId()             { return id; }
    public Usuario            getUsuario()        { return usuario; }
    public List<EmissaoFator> getFatores()        { return fatores; }
    public double             getTotalEmissao()   { return totalEmissao; }
    public String             getClassificacao()  { return classificacao; }
    public String             getDica()           { return dica; }
    public LocalDateTime      getDataCalculo()    { return dataCalculo; }

    public void setId(int id)                          { this.id            = id; }
    public void setFatores(List<EmissaoFator> fatores) { this.fatores       = fatores; }
    public void setTotalEmissao(double v)              { this.totalEmissao  = v; }
    public void setClassificacao(String c)             { this.classificacao = c; }
    public void setDica(String d)                      { this.dica          = d; }
    public void setDataCalculo(LocalDateTime dt)       { this.dataCalculo   = dt; }
}
