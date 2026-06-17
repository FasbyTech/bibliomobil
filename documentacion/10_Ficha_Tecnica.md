# Ficha Técnica - BiblioMobil

## 1. Identificación del Proyecto
- **Nombre del Proyecto**: BiblioMobil
- **Desarrollador**: FasbyTech
- **Categoría**: Herramientas / Gestión de Bibliotecas
- **Versión Actual**: 1.0.0
- **Estado**: Producción (Production-Ready)

## 2. Entorno de Ejecución
- **Plataforma**: Android
- **Versión Mínima (Min SDK)**: API 24 (Android 7.0 Nougat)
- **Versión Objetivo (Target SDK)**: API 35 (Android 15)
- **Versión de Compilación**: API 36

## 3. Stack Tecnológico (MAD - Modern Android Development)
- **Lenguaje**: Kotlin 2.4.0
- **UI Framework**: Jetpack Compose with Material Design 3
- **Arquitectura**: MVVM (Model-View-ViewModel) + Clean Architecture
- **Inyección de Dependencias**: Hilt (Dagger)
- **Motor de Base de Datos**: Room Persistence Library (SQLite con soporte FTS)
- **Procesamiento de Código**: KSP2 (Kotlin Symbol Processing)

## 4. Servicios Externos e Inteligencia Artificial
- **IA Generativa**: Google Gemini AI (Modelo: gemini-1.5-flash)
- **Visión Artificial**: Google ML Kit (Text Recognition / OCR)
- **Fuente Bibliográfica**: Google Books API
- **Gestión de Hardware**: CameraX (Cámara nativa)

## 5. Bibliotecas y Dependencias Clave
- **Red**: Retrofit 2.11.0 / OkHttp 5.4.0
- **Carga de Imágenes**: Coil 2.7.0 (Asíncrona con caché)
- **Navegación**: Jetpack Navigation Compose 2.8.8
- **Edición de Imagen**: Android-Image-Cropper 4.7.0 (CanHub/Vanniktech)
- **Logging**: OkHttp Logging Interceptor

## 6. Funcionalidades de Gestión de Datos
- **Gestión de Préstamos**: Control de disponibilidad física y trazabilidad de préstamos.
- **Crítica Personal**: Sistema de reseñas y valoraciones del usuario.
- **Exportación**: Generación de reportes en formato CSV.
- **Respaldo**: Sistema de Backup y Restauración de base de datos SQLite.
- **Integración Cloud**: Compatibilidad con Google Drive para almacenamiento de backups.

## 7. Seguridad y Protección de Datos
- **Protocolos**: Comunicación cifrada de extremo a extremo vía HTTPS (SSL/TLS).
- **Restricción de API**: Claves restringidas por Nombre de Paquete y Firma SHA-1.
- **Ofuscación**: Uso de ProGuard/R8 para protección del código fuente.
- **Privacidad**: Almacenamiento local (Offline-First). Sin recolección de PII (Personally Identifiable Information).

## 7. Requisitos de Hardware
- **Cámara**: Requerida para OCR y captura de portadas.
- **Micrófono**: Requerido para búsqueda por voz.
- **Conexión**: Necesaria para autocompletado y funciones de IA.
