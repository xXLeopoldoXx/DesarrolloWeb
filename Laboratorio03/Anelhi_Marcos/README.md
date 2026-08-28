# Laboratorio 03: Test-Driven Development (TDD) en Spring Boot

## 📋 Descripción General
Implementación de una API REST simple de productos aplicando la metodología **Test-Driven Development (TDD)** bajo el ciclo Red-Green-Refactor[cite: 1, 2]. El proyecto separa responsabilidades entre modelo, servicio y controlador, garantizando la calidad del software mediante pruebas automatizadas[cite: 1, 2].

## 🛠️ Tecnologías y Herramientas
* **Java 25**[cite: 1, 2]
* **Spring Boot** (Spring Web, Spring Boot Starter Test)[cite: 1, 2]
* **JUnit 5** y **AssertJ** para pruebas unitarias y aserciones[cite: 1, 2]
* **Mockito** / `@MockitoBean` para aislamiento de dependencias[cite: 1, 2]
* **MockMvc** para pruebas de controladores REST sin levantar un servidor real[cite: 1, 2]
* **Maven** para la gestión de dependencias y construcción[cite: 1, 2]

## 🏗️ Estructura del Proyecto
* **`model`**: Contiene la entidad `Producto` con sus atributos (id, nombre, precio, stock) y métodos de acceso[cite: 1, 2].
* **`service`**: Contiene la lógica de negocio en `ProductoService` y sus respectivas pruebas unitarias en `ProductoServiceTest`, validando reglas como precios mayores a cero, nombres obligatorios y stock no negativo[cite: 1, 2].
* **`controller`**: Expone los endpoints REST (`/productos`) en `ProductoController` validados mediante pruebas de integración web con `MockMvc` en `ProductoControllerTest`[cite: 1, 2].

## 🚀 Endpoints Principales
* `GET /productos`: Devuelve la lista completa de productos registrados en formato JSON[cite: 1, 2].
* `GET /productos/{id}`: Busca y retorna un producto específico por su ID o responde con código `404 Not Found` si no existe[cite: 1, 2].
* `POST /productos`: Registra un nuevo producto aplicando las validaciones de negocio y retorna el código `201 Created`[cite: 1, 2].

## 🧪 Ejecución de Pruebas
Para ejecutar todas las pruebas automatizadas del laboratorio desde la terminal, utiliza el siguiente comando de Maven[cite: 1, 2]:
```bash
mvn clean test