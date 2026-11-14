package com.carbonfootprint.model;

/**
 * HERANÇA
 * Emissão gerada pelo uso de transporte.
 * Herda de EmissaoFator e implementa o cálculo específico de transporte.
 */
public class TransporteEmissao extends EmissaoFator {

    public enum TipoTransporte {
        CARRO    ("Carro",          0.21),
        MOTO     ("Moto",           0.14),
        ONIBUS   ("Ônibus / Metrô", 0.07),
        BICICLETA("Bicicleta / Pé", 0.00);

        private final String  label;
        private final double  fatorKg; // kg CO₂ por km

        TipoTransporte(String label, double fatorKg) {
            this.label   = label;
            this.fatorKg = fatorKg;
        }

        public String  getLabel()   { return label; }
        public double  getFator()   { return fatorKg; }

        public static TipoTransporte fromLabel(String label) {
            for (TipoTransporte t : values())
                if (t.label.equalsIgnoreCase(label)) return t;
            return CARRO;
        }
    }

    private double         kmPorSemana;
    private TipoTransporte tipo;

    public TransporteEmissao(double kmPorSemana, TipoTransporte tipo) {
        super("Transporte",
              tipo.getLabel() + " — " + kmPorSemana + " km/semana",
              "km/semana");
        this.kmPorSemana = kmPorSemana;
        this.tipo        = tipo;
    }

    /** POLIMORFISMO — cálculo específico de transporte. */
    @Override
    public double calcular() {
        return kmPorSemana * tipo.getFator();
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────
    public double         getKmPorSemana() { return kmPorSemana; }
    public TipoTransporte getTipo()        { return tipo; }

    public void setKmPorSemana(double km)       { this.kmPorSemana = km; }
    public void setTipo(TipoTransporte tipo)     { this.tipo        = tipo; }
}
