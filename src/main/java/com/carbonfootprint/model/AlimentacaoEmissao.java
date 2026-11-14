package com.carbonfootprint.model;

/**
 * HERANÇA
 * Emissão gerada pelo padrão alimentar do usuário.
 */
public class AlimentacaoEmissao extends EmissaoFator {

    public enum PadraoAlimentar {
        MUITA_CARNE  ("Muita carne",  110.0),
        MODERADO     ("Moderado",      55.0),
        VEGETARIANO  ("Vegetariano",   22.0);

        private final String label;
        private final double kgCO2;

        PadraoAlimentar(String label, double kgCO2) {
            this.label = label;
            this.kgCO2 = kgCO2;
        }

        public String getLabel() { return label; }
        public double getKgCO2() { return kgCO2; }

        public static PadraoAlimentar fromLabel(String label) {
            for (PadraoAlimentar p : values())
                if (p.label.equalsIgnoreCase(label)) return p;
            return MODERADO;
        }
    }

    private PadraoAlimentar padrao;

    public AlimentacaoEmissao(PadraoAlimentar padrao) {
        super("Alimentação", padrao.getLabel(), "refeições/semana");
        this.padrao = padrao;
    }

    /** POLIMORFISMO — cálculo específico de alimentação. */
    @Override
    public double calcular() {
        return padrao.getKgCO2();
    }

    public PadraoAlimentar getPadrao()               { return padrao; }
    public void            setPadrao(PadraoAlimentar p) { this.padrao = p; }
}
