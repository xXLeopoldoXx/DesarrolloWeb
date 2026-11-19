package com.utp.proyectoriesgo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Autoevaluación hecha por una usuaria (módulo «Mi evaluación»). No confundir con {@link Evaluacion},
 * el caso que gestiona la administración. Guarda solo el resultado; las respuestas van aparte.
 */
@Entity
@Table(name = "evaluaciones_usuaria", indexes = @Index(columnList = "usuario_id, fecha"))
public class EvaluacionUsuaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    // La FK evita filas huérfanas mientras se elimina una cuenta.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false, nullable = false,
            foreignKey = @ForeignKey(name = "fk_evaluaciones_usuaria_usuario"))
    private Usuario usuaria;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private int puntaje;

    @Column(nullable = false, length = 16)
    private String nivel;

    @Column(name = "alerta_critica", nullable = false)
    private boolean alertaCritica;

    protected EvaluacionUsuaria() {
    }

    public EvaluacionUsuaria(Long usuarioId, LocalDateTime fecha, int puntaje, String nivel, boolean alertaCritica) {
        this.usuarioId = usuarioId;
        this.fecha = fecha;
        this.puntaje = puntaje;
        this.nivel = nivel;
        this.alertaCritica = alertaCritica;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public String getNivel() {
        return nivel;
    }

    public boolean isAlertaCritica() {
        return alertaCritica;
    }
}
