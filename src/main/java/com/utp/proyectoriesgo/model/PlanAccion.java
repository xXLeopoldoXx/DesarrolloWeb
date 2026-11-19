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

@Entity
@Table(name = "plan_acciones", indexes = @Index(columnList = "usuario_id, id"))
public class PlanAccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    // La FK restringe escrituras huérfanas mientras se elimina una cuenta.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false, nullable = false,
            foreignKey = @ForeignKey(name = "fk_plan_acciones_usuario"))
    private Usuario usuaria;

    @Column(nullable = false, length = 240)
    private String texto;

    @Column(nullable = false)
    private boolean completada;

    protected PlanAccion() {
    }

    public PlanAccion(Long usuarioId, String texto) {
        this.usuarioId = usuarioId;
        this.texto = texto;
    }

    public Long getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void marcar(boolean completada) {
        this.completada = completada;
    }
}
