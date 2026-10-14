package com.utp.proyectoriesgo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evaluaciones")
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seudonimo_victima", nullable = false, length = 120)
    private String seudonimoVictima;

    @Column(name = "edad_victima", nullable = false)
    private Integer edadVictima;

    @Column(name = "relacion_agresor", nullable = false, length = 80)
    private String relacionAgresor;

    @Column(name = "amenaza_muerte", nullable = false)
    private boolean amenazaMuerte;

    @Column(name = "violencia_fisica_previa", nullable = false)
    private boolean violenciaFisicaPrevia;

    @Column(name = "estrangulamiento_previo", nullable = false)
    private boolean estrangulamientoPrevio;

    @Column(name = "acceso_armas", nullable = false)
    private boolean accesoArmas;

    @Column(name = "incumplimiento_medida_proteccion", nullable = false)
    private boolean incumplimientoMedidaProteccion;

    @Column(name = "acoso_o_persecucion", nullable = false)
    private boolean acosoOPersecucion;

    @Column(name = "separacion_reciente", nullable = false)
    private boolean separacionReciente;

    @Column(name = "control_extremo", nullable = false)
    private boolean controlExtremo;

    @Column(name = "consumo_alcohol_drogas_agresor", nullable = false)
    private boolean consumoAlcoholDrogasAgresor;

    @Column(name = "denuncias_previas", nullable = false)
    private boolean denunciasPrevias;

    @Column(name = "hijos_en_comun", nullable = false)
    private boolean hijosEnComun;

    @Column(name = "puntaje_riesgo")
    private int puntajeRiesgo;

    @Column(name = "nivel_riesgo", length = 20)
    private String nivelRiesgo;

    @Column(name = "alerta_critica")
    private boolean alertaCritica;

    @Column(name = "fecha_ultimo_seguimiento")
    private LocalDateTime fechaUltimoSeguimiento;

    // Los factores relevantes se guardan como texto separado por comas,
    // porque una lista simple de String no se mapea directo a una columna.
    // Se convierte en getFactoresRelevantes() / setFactoresRelevantes().
    @Column(name = "factores_relevantes", columnDefinition = "TEXT")
    private String factoresRelevantesTexto;

    @Column(name = "fecha_registro")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeudonimoVictima() {
        return seudonimoVictima;
    }

    public void setSeudonimoVictima(String seudonimoVictima) {
        this.seudonimoVictima = seudonimoVictima;
    }

    public Integer getEdadVictima() {
        return edadVictima;
    }

    public void setEdadVictima(Integer edadVictima) {
        this.edadVictima = edadVictima;
    }

    public String getRelacionAgresor() {
        return relacionAgresor;
    }

    public void setRelacionAgresor(String relacionAgresor) {
        this.relacionAgresor = relacionAgresor;
    }

    public boolean isAmenazaMuerte() {
        return amenazaMuerte;
    }

    public void setAmenazaMuerte(boolean amenazaMuerte) {
        this.amenazaMuerte = amenazaMuerte;
    }

    public boolean isViolenciaFisicaPrevia() {
        return violenciaFisicaPrevia;
    }

    public void setViolenciaFisicaPrevia(boolean violenciaFisicaPrevia) {
        this.violenciaFisicaPrevia = violenciaFisicaPrevia;
    }

    public boolean isEstrangulamientoPrevio() {
        return estrangulamientoPrevio;
    }

    public void setEstrangulamientoPrevio(boolean estrangulamientoPrevio) {
        this.estrangulamientoPrevio = estrangulamientoPrevio;
    }

    public boolean isAccesoArmas() {
        return accesoArmas;
    }

    public void setAccesoArmas(boolean accesoArmas) {
        this.accesoArmas = accesoArmas;
    }

    public boolean isIncumplimientoMedidaProteccion() {
        return incumplimientoMedidaProteccion;
    }

    public void setIncumplimientoMedidaProteccion(boolean incumplimientoMedidaProteccion) {
        this.incumplimientoMedidaProteccion = incumplimientoMedidaProteccion;
    }

    public boolean isAcosoOPersecucion() {
        return acosoOPersecucion;
    }

    public void setAcosoOPersecucion(boolean acosoOPersecucion) {
        this.acosoOPersecucion = acosoOPersecucion;
    }

    public boolean isSeparacionReciente() {
        return separacionReciente;
    }

    public void setSeparacionReciente(boolean separacionReciente) {
        this.separacionReciente = separacionReciente;
    }

    public boolean isControlExtremo() {
        return controlExtremo;
    }

    public void setControlExtremo(boolean controlExtremo) {
        this.controlExtremo = controlExtremo;
    }

    public boolean isConsumoAlcoholDrogasAgresor() {
        return consumoAlcoholDrogasAgresor;
    }

    public void setConsumoAlcoholDrogasAgresor(boolean consumoAlcoholDrogasAgresor) {
        this.consumoAlcoholDrogasAgresor = consumoAlcoholDrogasAgresor;
    }

    public boolean isDenunciasPrevias() {
        return denunciasPrevias;
    }

    public void setDenunciasPrevias(boolean denunciasPrevias) {
        this.denunciasPrevias = denunciasPrevias;
    }

    public boolean isHijosEnComun() {
        return hijosEnComun;
    }

    public void setHijosEnComun(boolean hijosEnComun) {
        this.hijosEnComun = hijosEnComun;
    }

    public int getPuntajeRiesgo() {
        return puntajeRiesgo;
    }

    public void setPuntajeRiesgo(int puntajeRiesgo) {
        this.puntajeRiesgo = puntajeRiesgo;
    }

    public String getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(String nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }

    public boolean isAlertaCritica() {
        return alertaCritica;
    }

    public void setAlertaCritica(boolean alertaCritica) {
        this.alertaCritica = alertaCritica;
    }

    @Transient
    public List<String> getFactoresRelevantes() {
        if (factoresRelevantesTexto == null || factoresRelevantesTexto.isBlank()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(List.of(factoresRelevantesTexto.split(",")));
    }

    public void setFactoresRelevantes(List<String> factoresRelevantes) {
        this.factoresRelevantesTexto = (factoresRelevantes == null || factoresRelevantes.isEmpty())
                ? ""
                : String.join(",", factoresRelevantes);
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaUltimoSeguimiento() {
        return fechaUltimoSeguimiento;
    }

    public void setFechaUltimoSeguimiento(LocalDateTime fechaUltimoSeguimiento) {
        this.fechaUltimoSeguimiento = fechaUltimoSeguimiento;
    }
}