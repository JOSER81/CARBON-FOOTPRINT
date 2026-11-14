package com.carbonfootprint.model;

/**
 * HERANÇA
 * Emissão gerada pelo consumo de energia elétrica residencial.
 */
public class EnergiaEmissao extends EmissaoFator {

    public enum NivelConsumo {
        BAIXO ("Baixo",  45.0),
        MEDIO ("Médio",  95.0),
        ALTO  ("Alto",  190.0);

        private final String label;
        private final double kgCO2;

        NivelConsumo(String label, double kgCO2) {
            this.label = label;
            this.kgCO2 = kgCO2;
        }

        public String getLabel() { return label; }
        public double getKgCO2() { return kgCO2; }

        public static NivelConsumo fromLabel(String label) {
            for (NivelConsumo n : values())
                if (n.label.equalsIgnoreCase(label)) return n;
            return MEDIO;
        }
    }

    private NivelConsumo nivel;

    public EnergiaEmissao(NivelConsumo nivel) {
        super("Energia", "Consumo " + nivel.getLabel(), "kWh/mês");
        this.nivel = nivel;
    }

    /** POLIMORFISMO — cálculo específico de energia. */
    @Override
    public double calcular() {
        return nivel.getKgCO2();
    }

    public NivelConsumo getNivel()               { return nivel; }
    public void         setNivel(NivelConsumo n) { this.nivel = n; }
}
