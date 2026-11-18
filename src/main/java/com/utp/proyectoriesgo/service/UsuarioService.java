package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.RegistroRequest;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.exception.UsuarioExistenteException;
import com.utp.proyectoriesgo.model.Rol;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.repository.RolRepository;
import com.utp.proyectoriesgo.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Crea una usuaria nueva con el rol ROLE_USER. La contraseña se guarda con BCrypt. */
    @Transactional
    public Usuario registrar(RegistroRequest request) {
        String username = request.username().trim();

        if (usuarioRepository.findByUsername(username).isPresent()) {
            throw new UsuarioExistenteException("Ese nombre de usuario ya está en uso");
        }

        Rol rolUser = rolRepository.findByNombre("ROLE_USER")
                .orElseGet(() -> rolRepository.save(new Rol("ROLE_USER")));

        Usuario usuario = new Usuario(username, passwordEncoder.encode(request.password()), true);
        usuario.setSeudonimo(request.seudonimo().trim());
        usuario.setRoles(new HashSet<>(Set.of(rolUser)));
        return usuarioRepository.save(usuario);
    }

    /**
     * Devuelve la usuaria dueña de la petición actual.
     * TODOS los módulos "/api/mi/**" deben usar esto para filtrar por dueña
     * (nunca recibir el id de la usuaria desde el cliente).
     */
    @Transactional(readOnly = true)
    public Usuario actual(Authentication autenticacion) {
        return usuarioRepository.findByUsername(autenticacion.getName())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuaria no encontrada"));
    }
}
