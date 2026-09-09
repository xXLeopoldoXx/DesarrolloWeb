package com.utp.proyectoriesgo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

public class Evaluacion {

    private Long id;
    
    @NotBlank(message = "El seudónimo de la víctima es obligatorio")
    private String seudonimoVictima;
    
    @NotNull(message = "La edad es obligatoria")
    @Positive(message = "La edad debe ser un número positivo")
    private Integer edadVictima;
    
    @NotBlank(message = "La relación con el agresor no puede estar vacía")
    private String relacionAgresor;

    // Factores de riesgo
    private boolean amenazaMuerte;
    private boolean violenciaFisicaPrevia;
    private boolean estrangulamientoPrevio;
    private boolean accesoArmas;
    private boolean incumplimientoMedidaProteccion;
    private boolean acosoOPersecucion;
    private boolean separacionReciente;
    private boolean controlExtremo;
    private boolean consumoAlcoholDrogasAgresor;
    private boolean denunciasPrevias;
    private boolean hijosEnComun;

    // Resultado 
    private int puntajeRiesgo;
    private String nivelRiesgo;
    private boolean alertaCritica;
    private List<String> factoresRelevantes;
    private LocalDateTime fechaRegistro;

    public Evaluacion() {
    }

    public Evaluacion(Long id, String seudonimoVictima, Integer edadVictima, String relacionAgresor,
                       boolean amenazaMuerte, boolean violenciaFisicaPrevia, boolean estrangulamientoPrevio,
                       boolean accesoArmas, boolean incumplimientoMedidaProteccion, boolean acosoOPersecucion,
                       boolean separacionReciente, boolean controlExtremo, boolean consumoAlcoholDrogasAgresor,
                       boolean denunciasPrevias, boolean hijosEnComun) {
        this.id = id;
        this.seudonimoVictima = seudonimoVictima;
        this.edadVictima = edadVictima;
        this.relacionAgresor = relacionAgresor;
        this.amenazaMuerte = amenazaMuerte;
        this.violenciaFisicaPrevia = violenciaFisicaPrevia;
        this.estrangulamientoPrevio = estrangulamientoPrevio;
        this.accesoArmas = accesoArmas;
        this.incumplimientoMedidaProteccion = incumplimientoMedidaProteccion;
        this.acosoOPersecucion = acosoOPersecucion;
        this.separacionReciente = separacionReciente;
        this.controlExtremo = controlExtremo;
        this.consumoAlcoholDrogasAgresor = consumoAlcoholDrogasAgresor;
        this.denunciasPrevias = denunciasPrevias;
        this.hijosEnComun = hijosEnComun;
        this.fechaRegistro = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSeudonimoVictima() { return seudonimoVictima; }
    public void setSeudonimoVictima(String seudonimoVictima) { this.seudonimoVictima = seudonimoVictima; }

    public Integer getEdadVictima() { return edadVictima; }
    public void setEdadVictima(Integer edadVictima) { this.edadVictima = edadVictima; }

    public String getRelacionAgresor() { return relacionAgresor; }
    public void setRelacionAgresor(String relacionAgresor) { this.relacionAgresor = relacionAgresor; }

    public boolean isAmenazaMuerte() { return amenazaMuerte; }
    public void setAmenazaMuerte(boolean amenazaMuerte) { this.amenazaMuerte = amenazaMuerte; }

    public boolean isViolenciaFisicaPrevia() { return violenciaFisicaPrevia; }
    public void setViolenciaFisicaPrevia(boolean violenciaFisicaPrevia) { this.violenciaFisicaPrevia = violenciaFisicaPrevia; }

    public boolean isEstrangulamientoPrevio() { return estrangulamientoPrevio; }
    public void setEstrangulamientoPrevio(boolean estrangulamientoPrevio) { this.estrangulamientoPrevio = estrangulamientoPrevio; }

    public boolean isAccesoArmas() { return accesoArmas; }
    public void setAccesoArmas(boolean accesoArmas) { this.accesoArmas = accesoArmas; }

    public boolean isIncumplimientoMedidaProteccion() { return incumplimientoMedidaProteccion; }
    public void setIncumplimientoMedidaProteccion(boolean incumplimientoMedidaProteccion) { this.incumplimientoMedidaProteccion = incumplimientoMedidaProteccion; }

    public boolean isAcosoOPersecucion() { return acosoOPersecucion; }
    public void setAcosoOPersecucion(boolean acosoOPersecucion) { this.acosoOPersecucion = acosoOPersecucion; }

    public boolean isSeparacionReciente() { return separacionReciente; }
    public void setSeparacionReciente(boolean separacionReciente) { this.separacionReciente = separacionReciente; }

    public boolean isControlExtremo() { return controlExtremo; }
    public void setControlExtremo(boolean controlExtremo) { this.controlExtremo = controlExtremo; }

    public boolean isConsumoAlcoholDrogasAgresor() { return consumoAlcoholDrogasAgresor; }
    public void setConsumoAlcoholDrogasAgresor(boolean consumoAlcoholDrogasAgresor) { this.consumoAlcoholDrogasAgresor = consumoAlcoholDrogasAgresor; }

    public boolean isDenunciasPrevias() { return denunciasPrevias; }
    public void setDenunciasPrevias(boolean denunciasPrevias) { this.denunciasPrevias = denunciasPrevias; }

    public boolean isHijosEnComun() { return hijosEnComun; }
    public void setHijosEnComun(boolean hijosEnComun) { this.hijosEnComun = hijosEnComun; }

    public int getPuntajeRiesgo() { return puntajeRiesgo; }
    public void setPuntajeRiesgo(int puntajeRiesgo) { this.puntajeRiesgo = puntajeRiesgo; }

    public String getNivelRiesgo() { return nivelRiesgo; }
    public void setNivelRiesgo(String nivelRiesgo) { this.nivelRiesgo = nivelRiesgo; }

    public boolean isAlertaCritica() { return alertaCritica; }
    public void setAlertaCritica(boolean alertaCritica) { this.alertaCritica = alertaCritica; }

    public List<String> getFactoresRelevantes() { return factoresRelevantes; }
    public void setFactoresRelevantes(List<String> factoresRelevantes) { this.factoresRelevantes = factoresRelevantes; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}