package com.utp.proyectoriesgo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "seguimientos")
public class Seguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    private Evaluacion evaluacion;

    @Column(nullable = false, length = 500)
    private String nota;

    @Column(nullable = false)
    private LocalDateTime fecha;

    public Seguimiento() {
    }

    public Seguimiento(Evaluacion evaluacion, String nota, LocalDateTime fecha) {
        this.evaluacion = evaluacion;
        this.nota = nota;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public Evaluacion getEvaluacion() {
        return evaluacion;
    }

    public String getNota() {
        return nota;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}