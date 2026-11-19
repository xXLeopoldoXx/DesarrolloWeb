package com.utp.proyectoriesgo.dto;

/**
 * @param respuesta texto para mostrar a la usuaria
 * @param emergencia true si el mensaje indicó peligro: la respuesta es un texto fijo (no del modelo)
 *                   y el frontend muestra los botones para llamar
 */
public record AsistenteResponse(String respuesta, boolean emergencia) {
}
