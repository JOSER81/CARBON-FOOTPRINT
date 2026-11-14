package com.carbonfootprint.model;

/**
 * HERANÇA
 * Emissão gerada por hábitos de consumo (compras, produtos industrializados).
 */
public class ConsumoEmissao extends EmissaoFator {

    public enum FrequenciaCompra {
        ALTA     ("Frequente",  60.0),
        MODERADA ("Moderado",   30.0),
        BAIXA    ("Raramente",  10.0);

        private final String label;
        private final double kgCO2;

        FrequenciaCompra(String label, double kgCO2) {
            this.label = label;
            this.kgCO2 = kgCO2;
        }

        public String getLabel() { return label; }
        public double getKgCO2() { return kgCO2; }

        public static FrequenciaCompra fromLabel(String label) {
            for (FrequenciaCompra f : values())
                if (f.label.equalsIgnoreCase(label)) return f;
            return MODERADA;
        }
    }

    private FrequenciaCompra frequencia;

    public ConsumoEmissao(FrequenciaCompra frequencia) {
        super("Consumo", "Compras " + frequencia.getLabel(), "compras/mês");
        this.frequencia = frequencia;
    }

    /** POLIMORFISMO — cálculo específico de consumo. */
    @Override
    public double calcular() {
        return frequencia.getKgCO2();
    }

    public FrequenciaCompra getFrequencia()                { return frequencia; }
    public void             setFrequencia(FrequenciaCompra f) { this.frequencia = f; }
}
