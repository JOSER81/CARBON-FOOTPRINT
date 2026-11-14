package com.carbonfootprint.model;

/**
 * ABSTRAÇÃO / HERANÇA
 * Classe abstrata base que representa qualquer fator de emissão de CO₂.
 * Todas as categorias de emissão herdam desta classe e implementam calcular().
 */
public abstract class EmissaoFator {

    protected String nome;
    protected String descricao;
    protected String unidade;

    public EmissaoFator(String nome, String descricao, String unidade) {
        this.nome      = nome;
        this.descricao = descricao;
        this.unidade   = unidade;
    }

    /**
     * POLIMORFISMO — cada subclasse implementa sua própria lógica de cálculo.
     * @return emissão em kg de CO₂
     */
    public abstract double calcular();

    /**
     * Template Method — relatório padrão aproveitado por todas as subclasses.
     */
    public String gerarRelatorio() {
        return String.format("[%s] %.2f kg CO₂  (%s)", nome, calcular(), descricao);
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String getNome()      { return nome; }
    public String getDescricao() { return descricao; }
    public String getUnidade()   { return unidade; }

    @Override
    public String toString() {
        return gerarRelatorio();
    }
}
