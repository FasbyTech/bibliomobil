# BiblioMobil

## Descripción General
BiblioMobil es una aplicación Android nativa diseñada para la gestión inteligente de bibliotecas personales. La aplicación permite digitalizar colecciones físicas de forma eficiente mediante el escaneo de ISBN por OCR, enriqueciendo los datos automáticamente mediante Inteligencia Artificial (Gemini) y fuentes externas. Incluye un sistema completo de gestión de préstamos, valoraciones y reseñas personalizadas, manteniendo siempre un enfoque *offline-first* para garantizar la soberanía de la información del usuario.

## Stack Tecnológico
- **Lenguaje**: Kotlin 2.4.0
- **Arquitectura**: Clean Architecture + MVVM (Model-View-ViewModel)
- **UI**: Jetpack Compose con Material Design 3
- **IA**: Google Gemini 1.5 Flash (Generación de sinopsis y categorización)
- **OCR**: ML Kit (Reconocimiento de texto para ISBN)
- **APIs de Datos**: Google Books API (Primaria) + Open Library API (Fallback automático)
- **Persistencia**: Room (SQLite con soporte FTS5 para búsquedas rápidas)
- **Inyección de Dependencias**: Hilt
- **Red**: Retrofit + OkHttp (con sistema de reintentos resiliente)

## Instalación y Ejecución

### Requisitos Previos
- Dispositivo Android con API 24 (Android 7.0) o superior.
- Android Studio Ladybug o superior.
- Claves de API para **Google Books** y **Google Gemini** (vía Google Cloud Console / AI Studio).

### Pasos para la Instalación
1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/FasbyTech/bibliomobil.git
   ```
2. **Configurar claves secretas**:
   Cree un archivo `local.properties` en la raíz del proyecto y añada sus claves:
   ```properties
   google.books.api.key=TU_GOOGLE_BOOKS_KEY
   gemini.api.key=TU_GEMINI_API_KEY
   ```
3. **Sincronizar y Ejecutar**:
   Abra el proyecto en Android Studio, espere a que la sincronización de Gradle finalice y pulse en el botón "Run" para instalar en su dispositivo o emulador.

## Estructuración del Proyecto
El proyecto sigue los principios de **Clean Architecture**, organizado por capas y funcionalidades (feature-based):

- `data/`: Implementaciones de repositorios, DAOs de Room, entidades de base de datos y servicios de red (Retrofit).
- `domain/`: Lógica de negocio pura, definiciones de interfaces (repositorios) y casos de uso.
- `di/`: Módulos de Hilt para la inyección de dependencias.
- `navigation/`: Definición de rutas y grafo de navegación de la app.
- `ui/`: Componentes comunes, temas y SplashScreen.
- `features/` (Organizado por carpetas de funcionalidad):
    - `catalog/`: Listado general y búsqueda FTS5.
    - `camera/`: Escáner de ISBN con OCR y pre-visualización de cámara.
    - `add_volume/`: Formulario de registro y lógica de autocompletado/IA.
    - `volume_detail/`: Detalle del libro, gestión de préstamos y reseñas.
    - `collections/`: Gestión de agrupaciones de volúmenes.
    - `settings/`: Configuración de la app y sistema de backups.

## Funcionalidades Principales
- **Escaneo ISBN Inteligente**: Reconocimiento de códigos de barras y texto mediante la cámara con confirmación rápida (1.5s).
- **Autocompletado Resiliente**: Búsqueda secuencial de metadatos que prioriza Google Books y alterna a Open Library en caso de errores de red o falta de resultados.
- **Enriquecimiento por IA**: Generación de resúmenes y categorización automática mediante modelos generativos de Google Gemini.
- **Búsqueda por Voz**: Localización de libros en el catálogo mediante dictado natural.
- **Gestión de Préstamos**: Registro y trazabilidad de ejemplares prestados a contactos.
- **Offline-First**: Funcionamiento completo sin conexión tras la descarga inicial de metadatos.
- **Copias de Seguridad**: Sistema de exportación y restauración de la base de datos local para evitar la pérdida de información.
