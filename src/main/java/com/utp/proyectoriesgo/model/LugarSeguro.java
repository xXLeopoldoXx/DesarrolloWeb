package com.utp.proyectoriesgo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "lugares_seguros", indexes = @Index(columnList = "usuario_id, id"))
public class LugarSeguro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 250)
    private String referencia;

    @Column(length = 500)
    private String nota;

    protected LugarSeguro() {
    }

    public LugarSeguro(Long usuarioId, String nombre, String referencia, String nota) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.referencia = referencia;
        this.nota = nota;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getReferencia() {
        return referencia;
    }

    public String getNota() {
        return nota;
    }
}
