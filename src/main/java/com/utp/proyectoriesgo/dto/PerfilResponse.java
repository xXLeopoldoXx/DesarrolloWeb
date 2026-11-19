package com.utp.proyectoriesgo.dto;

import java.util.List;

public record PerfilResponse(
        Long id,
        String username,
        String seudonimo,
        List<String> roles) {
}
