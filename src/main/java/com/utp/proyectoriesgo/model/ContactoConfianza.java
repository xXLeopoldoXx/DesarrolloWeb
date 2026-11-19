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
@Table(name = "contactos_confianza", indexes = @Index(columnList = "usuario_id, id"))
public class ContactoConfianza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    // La FK restringe escrituras huérfanas mientras se elimina una cuenta.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false, nullable = false,
            foreignKey = @ForeignKey(name = "fk_contactos_confianza_usuario"))
    private Usuario usuaria;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 16)
    private String telefono;

    @Column(length = 100)
    private String relacion;

    protected ContactoConfianza() {
    }

    public ContactoConfianza(Long usuarioId, String nombre, String telefono, String relacion) {
        this.usuarioId = usuarioId;
        actualizar(nombre, telefono, relacion);
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getRelacion() {
        return relacion;
    }

    public void actualizar(String nombre, String telefono, String relacion) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.relacion = relacion;
    }
}
