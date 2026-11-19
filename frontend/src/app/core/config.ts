/**
 * Base de la API. Es relativa a propósito: en desarrollo `ng serve` la reenvía al backend
 * Spring Boot mediante proxy.conf.js (el puerto se cambia allí, con la variable API_PORT).
 */
export const API_URL = '/api';

/** Página a la que se envía a la usuaria con la "salida rápida". */
export const URL_SALIDA_RAPIDA = 'https://www.google.com';
