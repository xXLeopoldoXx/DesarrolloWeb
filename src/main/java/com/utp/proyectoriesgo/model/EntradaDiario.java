package com.utp.proyectoriesgo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "diario_entradas")
public class EntradaDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false)
    private LocalDateTime fecha;

    /**
     * Estado de ánimo en escala numérica de 1 (muy bajo/abrumada) a 5 (muy bien/tranquila).
     */
    @Column(name = "estado_animo", nullable = false)
    private int estadoAnimo;

    /**
     * Etiqueta descriptiva o emoción específica (ej. "Tranquila", "Esperanzada", "Ansiosa", etc.).
     */
    @Column(length = 50)
    private String emocion;

    @Column(nullable = false, length = 2000)
    private String contenido;

    public EntradaDiario() {
    }

    public EntradaDiario(Long usuarioId, String titulo, LocalDateTime fecha, int estadoAnimo, String emocion, String contenido) {
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.fecha = fecha;
        this.estadoAnimo = estadoAnimo;
        this.emocion = emocion;
        this.contenido = contenido;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getEstadoAnimo() {
        return estadoAnimo;
    }

    public void setEstadoAnimo(int estadoAnimo) {
        this.estadoAnimo = estadoAnimo;
    }

    public String getEmocion() {
        return emocion;
    }

    public void setEmocion(String emocion) {
        this.emocion = emocion;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
