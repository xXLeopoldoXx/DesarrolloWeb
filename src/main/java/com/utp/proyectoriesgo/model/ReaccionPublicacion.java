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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

/** Una reacción de apoyo de una usuaria a una publicación. Cada usuaria puede dar cada tipo una sola vez. */
@Entity
@Table(name = "reacciones_publicacion",
        uniqueConstraints = @UniqueConstraint(name = "uk_reaccion_usuaria_publicacion_tipo",
                columnNames = {"usuario_id", "publicacion_id", "tipo"}),
        indexes = @Index(columnList = "publicacion_id"))
public class ReaccionPublicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    // La FK evita filas huérfanas mientras se elimina una cuenta.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false, nullable = false,
            foreignKey = @ForeignKey(name = "fk_reacciones_usuaria"))
    private Usuario usuaria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publicacion_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_reacciones_publicacion"))
    private Publicacion publicacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TipoReaccion tipo;

    @Column(nullable = false)
    private LocalDateTime fecha;

    protected ReaccionPublicacion() {
    }

    public ReaccionPublicacion(Long usuarioId, Publicacion publicacion, TipoReaccion tipo) {
        this.usuarioId = usuarioId;
        this.publicacion = publicacion;
        this.tipo = tipo;
    }

    @PrePersist
    void antesDeGuardar() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public TipoReaccion getTipo() {
        return tipo;
    }
}
