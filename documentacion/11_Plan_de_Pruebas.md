# Plan de Pruebas (Testing) - BiblioMobil

Este documento detalla la estrategia de validación utilizada para garantizar la estabilidad, seguridad y funcionalidad de la aplicación.

## 1. Estrategia de Testing
Se ha seguido la **Pirámide de Pruebas** de Android, centrando los esfuerzos en tres niveles:

### A. Pruebas Unitarias (Unit Tests)
- **Localización**: `app/src/test`
- **Objetivo**: Validar la lógica de negocio aislada.
- **Casos probados**: 
    - Expresión regular de detección de ISBN (ISBN-10, ISBN-13 y variantes con 'X').
    - Lógica de formateo de autores y títulos.
    - Transformación de DTOs de la API a entidades de base de datos.

### B. Pruebas de Integración (Integration Tests)
- **Base de Datos (Room)**: Validación de transacciones atómicas al insertar volúmenes con múltiples autores. Comprobación de que la búsqueda `LIKE` devuelve los resultados esperados.
- **Red (Retrofit/OkHttp)**: Auditoría mediante `LoggingInterceptor` para verificar que los encabezados de seguridad (`X-Android-Cert`) se inyectan correctamente y que las respuestas 403 se gestionan.

### C. Pruebas de Interfaz de Usuario (UI Tests)
- **Herramienta**: Jetpack Compose Testing (JUnit4).
- **Casos probados**:
    - Navegación correcta entre el Catálogo y la pantalla de Detalles.
    - Apertura del selector de fuente de imagen (Cámara/Galería) al pulsar la portada.
    - Visualización de la barra de progreso de 3 segundos durante el escaneo.

## 2. Escenarios de Prueba Críticos (Manuales)
| Funcionalidad | Entrada | Resultado Esperado | Estado |
| :--- | :--- | :--- | :--- |
| Escaneo ISBN | Código 9788424116347 | Autocompletado de "El Quijote" tras 3s | **PASSED** |
| Préstamos | Nombre: "Juan" | El libro cambia a estado "Prestado" y guarda fecha | **PASSED** |
| Valoración | Pulsar 4 estrellas | El rating se actualiza y persiste localmente | **PASSED** |
| Búsqueda Voz | "Dragon Ball" | Filtrado instantáneo en el catálogo | **PASSED** |
| Recorte Portada | Foto de cámara | Editor de crop permite ajustar y guardar | **PASSED** |
| IA Generativa | Título: "Batman" | Gemini genera una sinopsis en español | **PASSED** |

## 3. Entorno de Pruebas
- **Dispositivo Físico**: Samsung Galaxy S21 (Android 14).
- **Emuladores**: Pixel 6 Pro (API 35) y Pixel 4 (API 24 - Min SDK).
