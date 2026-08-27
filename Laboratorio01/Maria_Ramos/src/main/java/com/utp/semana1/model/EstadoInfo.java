package com.utp.semana1.model;

public class EstadoInfo {
    private String aplicacion;
    private boolean activo;
    private String mensaje;

    public EstadoInfo(String aplicacion, boolean activo, String mensaje) {
        this.aplicacion = aplicacion;
        this.activo = activo;
        this.mensaje = mensaje;
    }

    public String getAplicacion() { return aplicacion; }
    public boolean isActivo() { return activo; }
    public String getMensaje() { return mensaje; }
}