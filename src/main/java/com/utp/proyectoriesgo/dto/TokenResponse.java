package com.utp.proyectoriesgo.dto;

public record TokenResponse(
        String tokenType,
        String accessToken,
        long expiresInSeconds) {
}
