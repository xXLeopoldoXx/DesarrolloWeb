package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.ContactoRequest;
import com.utp.proyectoriesgo.dto.ContactoResponse;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.ContactoConfianza;
import com.utp.proyectoriesgo.repository.ContactoConfianzaRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContactoServiceTest {
    private final ContactoConfianzaRepository contactos = mock(ContactoConfianzaRepository.class);
    private final ContactoService servicio = new ContactoService(contactos);
    private final ContactoRequest solicitud = new ContactoRequest(" Contacto de prueba ", "000000001", "  ");

    @Test
    void creaYActualizaUnContactoConCamposNormalizados() {
        var respuesta = new ContactoResponse(null, "Contacto de prueba", "000000001", null);
        when(contactos.save(any())).thenAnswer(consulta -> consulta.getArgument(0));
        when(contactos.findByIdAndUsuarioId(10L, 1L))
                .thenReturn(Optional.of(new ContactoConfianza(1L, "Anterior", "000000002", "Ejemplo")));

        assertThat(servicio.crear(1L, solicitud)).isEqualTo(respuesta);
        assertThat(servicio.actualizar(1L, 10L, solicitud)).isEqualTo(respuesta);
    }

    @Test
    void rechazaMutacionesAjenasYLimpiaPorUsuaria() {
        assertThatThrownBy(() -> servicio.actualizar(2L, 10L, solicitud))
                .isInstanceOf(RecursoNoEncontradoException.class);
        assertThatThrownBy(() -> servicio.eliminar(2L, 10L))
                .isInstanceOf(RecursoNoEncontradoException.class);

        servicio.eliminarDatosDe(1L);
        verify(contactos).deleteByUsuarioId(1L);
    }
}
