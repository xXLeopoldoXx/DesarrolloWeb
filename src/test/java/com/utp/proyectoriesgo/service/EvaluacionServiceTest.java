package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.EvaluacionRequest;
import com.utp.proyectoriesgo.exception.EvaluacionNoEncontradaException;
import com.utp.proyectoriesgo.model.Evaluacion;
import com.utp.proyectoriesgo.repository.EvaluacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionServiceTest {

    @Mock
    private EvaluacionRepository repository;

    @InjectMocks
    private EvaluacionService service;

    private EvaluacionRequest requestValido() {
        return new EvaluacionRequest(
                "Caso-Test", 28, "Pareja",
                true, true, true, true, true, true, true, true, true, true, true
        );
    }

    @BeforeEach
    void setUp() {
        service = new EvaluacionService(repository, new RiesgoCalculador());
    }

    @Test
    void crear_debeGuardarYCalcularRiesgo() {
        when(repository.save(any(Evaluacion.class))).thenAnswer(invocation -> {
            Evaluacion e = invocation.getArgument(0);
            e.setId(1L);
            return e;
        });

        Evaluacion creada = service.crear(requestValido());

        assertThat(creada.getId()).isEqualTo(1L);
        assertThat(creada.getNivelRiesgo()).isNotNull();
    }

    @Test
    void crear_conSeudonimoVacio_debeLanzarExcepcion() {
        EvaluacionRequest invalido = new EvaluacionRequest(
                "", 28, "Pareja",
                false, false, false, false, false, false, false, false, false, false, false
        );

        assertThatThrownBy(() -> service.crear(invalido))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void buscarPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(999L))
                .isInstanceOf(EvaluacionNoEncontradaException.class);
    }

    @Test
    void eliminar_cuandoNoExiste_debeLanzarExcepcion() {
        when(repository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.eliminar(999L))
                .isInstanceOf(EvaluacionNoEncontradaException.class);
    }
}
