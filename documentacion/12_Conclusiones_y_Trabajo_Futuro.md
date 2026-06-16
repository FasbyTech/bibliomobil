# Conclusiones y Trabajo Futuro

## 1. Conclusiones del Proyecto
El desarrollo de **BiblioMobil** ha demostrado que las tecnologías de vanguardia en Android (Kotlin 2.4.0, Compose, Gemini AI) permiten crear herramientas de gestión extremadamente potentes y accesibles en tiempos récord.

- **Cumplimiento de Objetivos**: Se ha logrado automatizar el proceso de catalogación mediante OCR y autocompletado inteligente.
- **Aprendizajes Clave**: La implementación de **Clean Architecture** ha sido fundamental para mantener la mantenibilidad del código, permitiendo integrar bibliotecas de terceros como el motor de recorte de imágenes o el reconocimiento de voz sin comprometer la estabilidad del sistema.
- **Impacto**: La app soluciona un problema real de organización personal, reduciendo la fricción manual en la entrada de datos.

## 2. Limitaciones Actuales
- **Dependencia de Red**: Aunque la app es *offline-first* para la consulta, requiere internet para el escaneo inicial y la IA.
- **Ámbito Geográfico**: La precisión de la búsqueda depende de la disponibilidad de metadatos en el índice de Google Books para cada región.

## 3. Trabajo Futuro (Roadmap)
Para escalar el proyecto tras el TFM, se proponen las siguientes líneas:
- **Red Social de Lectores**: Permitir a los usuarios compartir sus colecciones o recomendar libros a amigos mediante una capa de Firebase.
- **Exportación de Datos**: Implementar la generación de informes en CSV o PDF de la biblioteca personal.
- **Gamificación**: Añadir retos de lectura mensuales y medallas según el número de libros escaneados y leídos.
- **Sincronización Cloud**: Guardado de la biblioteca en la nube para sincronización entre múltiples dispositivos.
