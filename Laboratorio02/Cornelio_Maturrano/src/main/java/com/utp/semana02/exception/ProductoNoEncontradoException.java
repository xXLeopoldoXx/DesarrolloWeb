package com.utp.semana02.exception;
public class ProductoNoEncontradoException extends RuntimeException {
 public ProductoNoEncontradoException(Long id) {
 super("No existe un producto con id: " + id);
 }
}