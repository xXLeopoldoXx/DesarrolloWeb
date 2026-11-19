
package com.utp.proyectoriesgo.dto;

import java.time.LocalDateTime;

public record ComentarioResponse(

    Long id,
    Long publicacionId,
    String seudonimo,
    String texto,
    LocalDateTime fecha,
    boolean esMia

) {}
