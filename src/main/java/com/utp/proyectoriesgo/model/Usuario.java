package com.utp.proyectoriesgo.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String username;

    // Aquí se guarda el HASH BCrypt (60 caracteres), nunca la contraseña real.
    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false)
    private boolean activo = true;

    // Nombre con el que se muestra la usuaria. Nullable: las cuentas anteriores no lo tienen
    // (con ddl-auto=update no se puede agregar una columna NOT NULL a una tabla con filas).
    @Column(length = 40)
    private String seudonimo;

    // EAGER: los roles se necesitan siempre al autenticar.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuarios_roles", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Rol> roles = new HashSet<>();

    public Usuario() {
    }

    public Usuario(String username, String password, boolean activo) {
        this.username = username;
        this.password = password;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isActivo() {
        return activo;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public String getSeudonimo() {
        return seudonimo;
    }

    public void setSeudonimo(String seudonimo) {
        this.seudonimo = seudonimo;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }
}