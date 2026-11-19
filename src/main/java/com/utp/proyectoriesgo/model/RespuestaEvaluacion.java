package com.utp.proyectoriesgo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Una respuesta (factor + Sí/No/No estoy segura) de una {@link EvaluacionUsuaria}. */
@Entity
@Table(name = "respuestas_evaluacion", indexes = @Index(columnList = "evaluacion_id"))
public class RespuestaEvaluacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evaluacion_id", nullable = false)
    private Long evaluacionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id", insertable = false, updatable = false, nullable = false,
            foreignKey = @ForeignKey(name = "fk_respuestas_evaluacion_evaluacion"))
    private EvaluacionUsuaria evaluacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Factor factor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Respuesta respuesta;

    protected RespuestaEvaluacion() {
    }

    public RespuestaEvaluacion(Long evaluacionId, Factor factor, Respuesta respuesta) {
        this.evaluacionId = evaluacionId;
        this.factor = factor;
        this.respuesta = respuesta;
    }

    public Long getId() {
        return id;
    }

    public Long getEvaluacionId() {
        return evaluacionId;
    }

    public Factor getFactor() {
        return factor;
    }

    public Respuesta getRespuesta() {
        return respuesta;
    }
}
