package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.RegistroRequest;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.exception.UsuarioExistenteException;
import com.utp.proyectoriesgo.model.Rol;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.repository.RolRepository;
import com.utp.proyectoriesgo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    private UsuarioService service;

    @BeforeEach
    void preparar() {
        service = new UsuarioService(usuarioRepository, rolRepository, new BCryptPasswordEncoder());
    }

    private RegistroRequest solicitud() {
        return new RegistroRequest("maria.l", "Clave1234", "  Luna ");
    }

    @Test
    void registrar_guardaHashYRolUserYSeudonimoLimpio() {
        when(usuarioRepository.findByUsername("maria.l")).thenReturn(Optional.empty());
        when(rolRepository.findByNombre("ROLE_USER")).thenReturn(Optional.of(new Rol("ROLE_USER")));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        service.registrar(solicitud());

        var captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario guardado = captor.getValue();

        assertEquals("maria.l", guardado.getUsername());
        assertEquals("Luna", guardado.getSeudonimo());
        assertNotEquals("Clave1234", guardado.getPassword());
        assertTrue(guardado.getPassword().startsWith("$2"), "debe ser un hash BCrypt");
        assertEquals(1, guardado.getRoles().size());
        assertEquals("ROLE_USER", guardado.getRoles().iterator().next().getNombre());
    }

    @Test
    void registrar_conUsuarioExistente_lanzaConflictoYNoGuarda() {
        when(usuarioRepository.findByUsername("maria.l"))
                .thenReturn(Optional.of(new Usuario("maria.l", "hash", true)));

        assertThrows(UsuarioExistenteException.class, () -> service.registrar(solicitud()));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void actual_devuelveLaUsuariaDelToken() {
        Usuario usuario = new Usuario("maria.l", "hash", true);
        when(usuarioRepository.findByUsername("maria.l")).thenReturn(Optional.of(usuario));

        var auth = UsernamePasswordAuthenticationToken.authenticated("maria.l", null, java.util.List.of());

        assertEquals(usuario, service.actual(auth));
    }

    @Test
    void actual_siNoExiste_lanzaNoEncontrado() {
        when(usuarioRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

        var auth = UsernamePasswordAuthenticationToken.authenticated("fantasma", null, java.util.List.of());

        assertThrows(RecursoNoEncontradoException.class, () -> service.actual(auth));
    }
}
