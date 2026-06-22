# Guion de Presentación (Slides) - BiblioMobil

Este documento sirve como storyboard y guion para la creación de las diapositivas de defensa del TFM. Cada sección representa una diapositiva sugerida.

---

## Diapositiva 1: Portada
- **Título**: BiblioMobil: Gestión Inteligente de Bibliotecas Personales
- **Subtítulo**: TFM - Máster en Desarrollo con IA
- **Presentado por**: [Tu Nombre]
- **Visual**: Logo de BiblioMobil (icono de libro animado).

## Diapositiva 2: El Problema
- **Puntos clave**:
    - Fricción en la entrada manual de datos.
    - Pérdida de libros por falta de trazabilidad en préstamos.
    - Bibliotecas físicas "desconectadas" del mundo digital.
- **Visual**: Imagen de una estantería desordenada vs. un cronómetro.

## Diapositiva 3: La Solución
- **Puntos clave**:
    - Digitalización en <5 segundos.
    - Enriquecimiento automático mediante IA.
    - Control total del ejemplar físico.
- **Visual**: Screenshot de la pantalla principal (Catálogo).

## Diapositiva 4: Funcionalidades Core
- **Puntos clave**:
    - **OCR Inteligente**: Escaneo de ISBN con ventana de confirmación.
    - **Búsqueda por Voz**: Accesibilidad total.
    - **IA Generativa**: Resúmenes dinámicos con Gemini 1.5 Flash.
- **Visual**: Collage de las pantallas de Escáner y Búsqueda por voz.

## Diapositiva 5: Stack Tecnológico (MAD)
- **Tecnologías**: Kotlin 2.4.0, Jetpack Compose, KSP2, Hilt.
- **Persistencia**: Room FTS5 (Búsqueda optimizada).
- **Red**: Retrofit + Interceptores de Seguridad.
- **Visual**: Iconos de Kotlin, Compose y Google Cloud.

## Diapositiva 6: Arquitectura (Clean Architecture)
- **Capas**: Data, Domain, UI.
- **Patrón**: MVVM con flujos reactivos (StateFlow).
- **Justificación**: Mantenibilidad y desacoplamiento.
- **Visual**: Diagrama de capas (Cebolla/Hexagonal).

## Diapositiva 7: Inteligencia Artificial (Gemini)
- **Uso**: Generación de sinopsis y categorización.
- **Seguridad**: Implementación de Safety Settings.
- **Eficiencia**: Uso del modelo Flash para baja latencia.
- **Visual**: Ejemplo de un "Antes" (sin sinopsis) y "Después" (generado por IA).

## Diapositiva 8: Gestión de Préstamos y Valoración
- **Puntos clave**:
    - Relaciones 1:N en Room.
    - Trazabilidad: ¿Quién tiene mi libro?
    - Valoración interactiva y reseñas personales.
- **Visual**: Screenshot de la pantalla de Detalle con el historial de préstamos.

## Diapositiva 9: Soberanía de Datos y Seguridad
- **Puntos clave**:
    - **Offline-First**: Privacidad garantizada.
    - **Backup/Restore**: Integración con Google Drive.
    - **Seguridad**: Restricción de APIs por firma SHA-1.
- **Visual**: Icono de escudo + Logo de Google Drive.

## Diapositiva 10: Plan de Pruebas
- **Metodología**: Pirámide de pruebas (Unit, Integration, UI).
- **Resultado**: 100% de escenarios críticos validados en dispositivos reales.
- **Visual**: Captura de los resultados de los tests en Android Studio.

## Diapositiva 11: Conclusiones y Futuro
- **Logro**: Producto profesional listo para despliegue.
- **Evolución**: Sincronización cloud automática y comunidad social de lectores.
- **Visual**: Imagen de una biblioteca digital infinita.

## Diapositiva 12: ¡Muchas Gracias!
- Espacio para preguntas del tribunal.
- Datos de contacto y enlace al repositorio de GitHub.
