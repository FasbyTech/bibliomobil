# Memoria Técnica: BiblioMobil

## 1. Resumen Ejecutivo
BiblioMobil es una aplicación Android nativa diseñada para la gestión inteligente de bibliotecas personales. Utiliza OCR para el escaneo de ISBN, Inteligencia Artificial (Gemini) para el enriquecimiento de datos y un sistema de préstamos para la trazabilidad de ejemplares físicos. El sistema permite una gestión integral de volúmenes y colecciones, incluyendo la eliminación segura de registros, valoraciones interactivas por estrellas y reseñas personales personalizadas.

## 2. Justificación de Necesidad
El proyecto resuelve la fricción en la digitalización de colecciones físicas, eliminando la entrada manual de datos y garantizando la soberanía de la información mediante un modelo *offline-first*.

## 3. Stack Tecnológico (MAD)
- **Lenguaje**: Kotlin 2.4.0
- **Arquitectura**: Clean Architecture + MVVM
- **UI**: Jetpack Compose + Material Design 3
- **IA**: Google Gemini 1.5 Flash + ML Kit OCR
- **Persistencia**: Room (SQLite FTS4/5)
- **Inyección de Dependencias**: Hilt

## 4. Arquitectura y Seguridad
La app sigue los principios SOLID y Clean Architecture, separando las responsabilidades en capas de UI, Domain y Data. La seguridad se garantiza mediante la restricción de API Keys con firmas SHA-1 y el uso de FileProvider para la gestión de backups.

## 5. Pruebas y Validación
Se han realizado pruebas unitarias de lógica de negocio y pruebas de integración sobre la base de datos Room. Los escenarios críticos como el escaneo de ISBN y el registro de préstamos han sido validados con éxito en dispositivos reales.

## 6. Conclusiones y Trabajo Futuro
BiblioMobil cumple los objetivos de automatización y accesibilidad. Como líneas futuras se plantea la sincronización cloud automática y la integración de funcionalidades sociales.
