
package com.utp.proyectoriesgo.dto;

import java.time.LocalDateTime;

public record ReporteComunidadResponse(
    Long id,
    Long publicacionId,
    String autorPublicacion,
    String textoPublicacion,
    String motivo,
    LocalDateTime fecha
) {
}
