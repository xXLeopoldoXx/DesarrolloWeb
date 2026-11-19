package com.utp.proyectoriesgo.model;

/** Los 9 factores de la autoevaluación, con el texto que se muestra y el peso que suma al marcar «Sí». */
public enum Factor {
    AMENAZA_MUERTE("Amenaza de muerte", 25),
    ACCESO_ARMAS("Acceso a armas", 20),
    ESTRANGULAMIENTO_PREVIO("Estrangulamiento previo", 30),
    DENUNCIAS_PREVIAS("Denuncias previas", 10),
    INCUMPLIMIENTO_MEDIDA_PROTECCION("Incumplimiento de medida de protección", 15),
    ACOSO_O_PERSECUCION("Acoso o persecución", 15),
    SEPARACION_RECIENTE("Separación reciente", 10),
    CONTROL_EXTREMO("Control extremo", 10),
    CONSUMO_ALCOHOL_DROGAS_AGRESOR("Consumo problemático del agresor", 5);

    private final String texto;
    private final int peso;

    Factor(String texto, int peso) {
        this.texto = texto;
        this.peso = peso;
    }

    public String getTexto() {
        return texto;
    }

    public int getPeso() {
        return peso;
    }
}
