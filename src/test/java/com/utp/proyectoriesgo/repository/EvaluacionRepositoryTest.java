package com.utp.proyectoriesgo.repository;

import com.utp.proyectoriesgo.model.Evaluacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EvaluacionRepositoryTest {

    @Autowired
    private EvaluacionRepository repository;

    @Test
    void debeGuardarYRecuperarEvaluacion() {
        Evaluacion evaluacion = new Evaluacion(
                null, "Caso-Integracion", 30, "Pareja",
                true, false, false, false, false, false, false, false, false, false, false
        );

        Evaluacion guardada = repository.save(evaluacion);

        assertThat(guardada.getId()).isNotNull();
        assertThat(repository.findById(guardada.getId())).isPresent();
    }
}