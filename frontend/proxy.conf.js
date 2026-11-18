/**
 * Proxy del servidor de desarrollo (ng serve): reenvía /api/** al backend Spring Boot.
 * Así Angular llama a una ruta relativa ("/api/...") y no hay problemas de CORS en desarrollo.
 *
 * Si el puerto 8080 está ocupado en tu máquina (por ejemplo por Jenkins):
 *   1) arranca el backend con la variable SERVER_PORT=8081
 *   2) arranca Angular con la variable API_PORT=8081   (PowerShell:  $env:API_PORT=8081; npm start)
 */
const puerto = process.env.API_PORT || '8080';

module.exports = {
  '/api': {
    target: `http://localhost:${puerto}`,
    secure: false,
    changeOrigin: true
  }
};
