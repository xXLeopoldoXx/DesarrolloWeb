# Evidencias del Laboratorio 15

Verificación del 27 de noviembre de 2026. Se usaron Chrome con Angular, solicitudes HTTP directas a Spring Boot y consultas SQL mediante psql sobre PostgreSQL 18.6 temporal. Las cuentas, los productos y los movimientos son ficticios. Las capturas muestran la interfaz sin contraseñas ni valores de JWT.

## Matriz de pruebas

| ID | Caso | Resultado esperado | Resultado observado |
| --- | --- | --- | --- |
| P01 | Inicio de sesión válido | Login y perfil disponibles | POST login 200, GET perfil 200 con rol ADMIN y acceso al catálogo. |
| P02 | Contraseña incorrecta | 401 con un mensaje de acceso | POST login 401 y mensaje de credenciales incorrectas. |
| P03 | USER consulta el catálogo | 200 y lectura del catálogo | GET catálogo 200 con dos productos. Los enlaces de alta, salida e indicadores no aparecen. |
| P04 | USER filtra de 100 a 1000 | 200 con productos en el rango | GET precios 200 con el monitor de S/ 450. El cable de S/ 25 quedó fuera. |
| P05 | ADMIN crea un producto | 201 y persistencia | POST productos 201. SQL confirmó el monitor con stock inicial 10. |
| P06 | Formulario de alta con nombre vacío | Mensaje sin enviar POST | Mensaje de campo obligatorio y cero POST. |
| P07 | ADMIN registra una salida de 2 | 200, catálogo actualizado y movimiento persistido | POST 200 con cabecera Bearer. Stock de 10 a 8 en la interfaz y en PostgreSQL. Un movimiento SALIDA de 2 unidades. |
| P08 | Cantidad 0, negativa o decimal | Validación de cliente y servidor | Angular bloqueó 0, -1 y 1,5. La API respondió 400 a 0 y 1.5. |
| P09 | Cantidad mayor al stock | 409 y datos intactos | Cantidad 11 bloqueada por la interfaz. La API respondió 409 y la base conservó stock 10 y cero movimientos. |
| P10 | USER intenta descontar stock | 403 y datos intactos | El guard informó que la operación requiere ADMIN. El POST directo devolvió 403. SQL confirmó stock 8 y un solo movimiento previo. |
| P11 | Petición sin JWT | 401 | GET catálogo 401. |
| P12 | JWT inválido | 401 y sesión local descartada | GET catálogo 401. Angular limpió la sesión y mostró el aviso para iniciar sesión otra vez. |
| P13 | Recurso inexistente | 404 y mensaje comprensible | Ruta de API inexistente 404. El formulario mostró el mensaje de producto no encontrado al recibir otro 404. |
| P14 | Red interrumpida o backend apagado | Mensaje de servicio inaccesible | La interrupción de red produjo status 0 en Angular y el backend apagado produjo 502 del proxy. Ambos mostraron el aviso con opción de reintentar. |
| P15 | Recargar después del login | Volver a iniciar sesión | La recarga de una ruta protegida redirigió a login, porque el token estaba en memoria. |
| P16 | Rollback ante un fallo controlado | Stock y movimiento revertidos | La prueba de integración sobre PostgreSQL provocó un fallo tras el descuento y el registro. Una transacción nueva leyó el stock original y ningún movimiento nuevo. |

### Comprobaciones adicionales

- Dos salidas concurrentes de 12 unidades contra un stock de 15 obtuvieron 200 y 409. PostgreSQL conservó un solo movimiento y stock 3. La interfaz gestionó otro 409 con el stock que tenía en pantalla y permitió actualizarlo.
- El panel de indicadores cargó con rol ADMIN. Un filtro sin coincidencias mostró el estado vacío.
- El formulario de salida se verificó a 390 px de ancho. Se comprobó con teclado el acceso directo al contenido principal.
- Maven aprobó 16 pruebas y generó el JAR. Angular aprobó 6 pruebas con Vitest y generó la compilación de producción.

## Consultas de PostgreSQL

Ejecuta estas consultas antes y después de registrar una salida para comparar el producto y sus movimientos.

```sql
SELECT id, nombre, stock
FROM productos
WHERE nombre = 'Monitor de laboratorio';

SELECT m.tipo, m.cantidad
FROM movimientos_stock AS m
JOIN productos AS p ON p.id = m.producto_id
WHERE p.nombre = 'Monitor de laboratorio'
ORDER BY m.id;
```

**Resultado observado:** antes de la salida, stock 10 y ningún movimiento. Después, stock 8 y un movimiento `SALIDA | 2`.

## Capturas reales del navegador

1. [Acceso](frontend/01-acceso.png)
2. [Validación de cantidad](frontend/02-validacion-salida.png)
3. [Formulario de salida](frontend/03-salida-stock.png)
4. [Catálogo con stock actualizado](frontend/04-catalogo-actualizado.png)
5. [Indicadores de administración](frontend/05-indicadores-admin.png)
6. [Catálogo filtrado con rol USER](frontend/06-filtro-user.png)
7. [Formulario en pantalla móvil](frontend/07-salida-en-movil.png)
8. [Aviso con backend apagado](frontend/08-api-inaccesible.png)
