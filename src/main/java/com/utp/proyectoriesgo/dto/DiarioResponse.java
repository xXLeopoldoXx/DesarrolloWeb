package com.utp.proyectoriesgo.dto;

import java.time.LocalDateTime;

public record DiarioResponse(
        Long id,
        Long usuarioId,
        String titulo,
        LocalDateTime fecha,
        int estadoAnimo,
        String emocion,
        String contenido
) {
}
