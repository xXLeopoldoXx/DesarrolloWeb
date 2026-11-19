package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.DiarioRequest;
import com.utp.proyectoriesgo.dto.DiarioResponse;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.service.DiarioService;
import com.utp.proyectoriesgo.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/mi/diario")
public class MiDiarioController {

    private final DiarioService diarioService;
    private final UsuarioService usuarioService;

    public MiDiarioController(DiarioService diarioService, UsuarioService usuarioService) {
        this.diarioService = diarioService;
        this.usuarioService = usuarioService;
    }

    /**
     * Lista únicamente las entradas pertenecientes a la usuaria autenticada.
     */
    @GetMapping
    public List<DiarioResponse> listar(Authentication authentication) {
        Usuario usuaria = usuarioService.actual(authentication);
        return diarioService.listar(usuaria.getId());
    }

    /**
     * Obtiene el detalle de una entrada. Si el ID pertenece a otra usuaria o no existe, retorna 404.
     */
    @GetMapping("/{id}")
    public DiarioResponse obtenerPorId(@PathVariable Long id, Authentication authentication) {
        Usuario usuaria = usuarioService.actual(authentication);
        return diarioService.obtenerPorId(id, usuaria.getId());
    }

    /**
     * Crea una nueva entrada en el diario personal de la usuaria autenticada.
     */
    @PostMapping
    public ResponseEntity<DiarioResponse> crear(@Valid @RequestBody DiarioRequest request,
                                                Authentication authentication) {
        Usuario usuaria = usuarioService.actual(authentication);
        DiarioResponse creada = diarioService.crear(usuaria.getId(), request);
        URI location = URI.create("/api/mi/diario/" + creada.id());
        return ResponseEntity.created(location).body(creada);
    }

    /**
     * Modifica una entrada propia. Si la entrada no pertenece a la usuaria, retorna 404.
     */
    @PutMapping("/{id}")
    public DiarioResponse actualizar(@PathVariable Long id,
                                    @Valid @RequestBody DiarioRequest request,
                                    Authentication authentication) {
        Usuario usuaria = usuarioService.actual(authentication);
        return diarioService.actualizar(id, usuaria.getId(), request);
    }

    /**
     * Elimina una entrada propia. Si la entrada no pertenece a la usuaria, retorna 404.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication authentication) {
        Usuario usuaria = usuarioService.actual(authentication);
        diarioService.eliminar(id, usuaria.getId());
        return ResponseEntity.noContent().build();
    }
}
