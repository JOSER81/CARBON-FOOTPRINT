package com.carbonfootprint.model;

/**
 * HERANÇA
 * Emissão gerada pelo descarte de resíduos sólidos.
 */
public class ResiduosEmissao extends EmissaoFator {

    public enum NivelReciclagem {
        SEM_RECICLAGEM    ("Sem reciclagem",    40.0),
        ALGUMA_RECICLAGEM ("Alguma reciclagem", 20.0),
        RECICLAGEM_ATIVA  ("Reciclagem ativa",   8.0);

        private final String label;
        private final double kgCO2;

        NivelReciclagem(String label, double kgCO2) {
            this.label = label;
            this.kgCO2 = kgCO2;
        }

        public String getLabel() { return label; }
        public double getKgCO2() { return kgCO2; }

        public static NivelReciclagem fromLabel(String label) {
            for (NivelReciclagem r : values())
                if (r.label.equalsIgnoreCase(label)) return r;
            return ALGUMA_RECICLAGEM;
        }
    }

    private NivelReciclagem nivel;

    public ResiduosEmissao(NivelReciclagem nivel) {
        super("Resíduos", nivel.getLabel(), "kg lixo/semana");
        this.nivel = nivel;
    }

    /** POLIMORFISMO — cálculo específico de resíduos. */
    @Override
    public double calcular() {
        return nivel.getKgCO2();
    }

    public NivelReciclagem getNivel()                { return nivel; }
    public void            setNivel(NivelReciclagem n) { this.nivel = n; }
}
