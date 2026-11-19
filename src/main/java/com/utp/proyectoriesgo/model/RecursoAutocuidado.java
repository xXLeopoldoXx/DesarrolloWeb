package com.utp.proyectoriesgo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "recursos_autocuidado")
public class RecursoAutocuidado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Tipo de recurso: CONSEJO, EJERCICIO, GUIA, ENLACE, etc.
     */
    @Column(nullable = false, length = 50)
    private String tipo;

    /**
     * Enlace web externo de referencia o consejo práctico detallado.
     */
    @Column(name = "enlace_o_consejo", length = 500)
    private String enlaceOConsejo;

    public RecursoAutocuidado() {
    }

    public RecursoAutocuidado(String categoria, String titulo, String descripcion, String tipo, String enlaceOConsejo) {
        this.categoria = categoria;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.enlaceOConsejo = enlaceOConsejo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEnlaceOConsejo() {
        return enlaceOConsejo;
    }

    public void setEnlaceOConsejo(String enlaceOConsejo) {
        this.enlaceOConsejo = enlaceOConsejo;
    }
}
