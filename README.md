# 📚 BiblioMobil - Gestión Inteligente de Bibliotecas

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.0-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-API%2024+-green.svg?style=flat&logo=android)](https://www.android.com)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-orange.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Hilt](https://img.shields.io/badge/DI-Hilt-yellow.svg?style=flat)](https://dagger.dev/hilt/)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](LICENSE)

**BiblioMobil** es una aplicación Android nativa de vanguardia diseñada para la gestión inteligente de bibliotecas personales. Permite digitalizar colecciones físicas mediante OCR, enriquecer metadatos con IA y gestionar préstamos, todo bajo un enfoque *offline-first*.

---

## 📖 Descripción General

Digitaliza tu biblioteca en segundos. **BiblioMobil** elimina la carga manual de datos permitiendo el escaneo rápido de ISBN. La aplicación utiliza la potencia de **Google Gemini** para generar sinopsis y categorizar tus libros automáticamente, consultando múltiples fuentes internacionales para garantizar la exactitud de la información.

---

## 🛠️ Stack Tecnológico

| Componente | Tecnología |
| :--- | :--- |
| **Lenguaje** | Kotlin 2.4.0 |
| **Arquitectura** | Clean Architecture + MVVM |
| **UI Framework** | Jetpack Compose (Material Design 3) |
| **IA Generativa** | Google Gemini 1.5 Flash |
| **Visión Artificial** | ML Kit (OCR para ISBN) |
| **Persistencia** | Room (SQLite + FTS5 para búsquedas ultrarrápidas) |
| **Red** | Retrofit + OkHttp (Estrategia de reintentos resiliente) |
| **Inyección de Dependencias** | Hilt |

---

## 🚀 Instalación y Ejecución

### Requisitos Previos
*   Dispositivo Android con **API 24 (7.0)** o superior.
*   **Android Studio Ladybug** o superior.
*   API Keys de Google Cloud (Books API) y AI Studio (Gemini API).

### Pasos para la Instalación

1.  **Clonar el repositorio**:
    ```bash
    git clone https://github.com/FasbyTech/bibliomobil.git
    ```

2.  **Configurar claves secretas**:
    Crea un archivo `local.properties` en la raíz del proyecto:
    ```properties
    google.books.api.key=TU_GOOGLE_BOOKS_KEY
    gemini.api.key=TU_GEMINI_API_KEY
    ```

3.  **Sincronizar y Ejecutar**:
    Abre el proyecto en Android Studio, sincroniza Gradle y pulsa **Run**.

---

## 🏗️ Estructura del Proyecto

El código está organizado siguiendo los principios de **Clean Architecture**, dividiéndose por capas y funcionalidades:

```text
app/src/main/java/com/fasby/bibliomobil/
├── data/           # Implementaciones de Repositorios, DAOs y API Services
├── domain/         # Modelos de negocio, Interfaces y Casos de Uso
├── di/             # Módulos de Hilt (Inyección de dependencias)
├── navigation/     # Grafo de navegación y rutas de Compose
├── ui/             # Componentes comunes, Temas y SplashScreen
└── features/       # Módulos por funcionalidad
    ├── catalog/    # Listado principal y búsqueda FTS5
    ├── camera/     # Escáner OCR de alta velocidad (1.5s)
    ├── add_volume/ # Lógica de registro y autocompletado con IA
    ├── volume_detail/# Gestión de préstamos, valoraciones y reseñas
    └── collections/# Agrupación y gestión de colecciones
```

---

## ✨ Funcionalidades Estrella

*   **⚡ Escaneo Ultra-Rápido**: Reconocimiento de ISBN por cámara con confirmación visual en solo **1.5 segundos**.
*   **🛡️ Autocompletado Resiliente**: Búsqueda secuencial (Google Books ➡️ Open Library) para fallos de red.
*   **🧠 Enriquecimiento por IA**: Generación de sinopsis atractivas y categorización automática mediante **Gemini**.
*   **🎙️ Búsqueda por Voz**: Localiza cualquier libro en tu catálogo simplemente dictando su nombre.
*   **🤝 Gestión de Préstamos**: Controla a quién has prestado tus libros con historial detallado.
*   **🔌 Offline-First**: Acceso total a tu biblioteca sin necesidad de internet.
*   **💾 Backups**: Sistema de exportación y restauración para la soberanía de tus datos.

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.

---
*Desarrollado por [FasbyTech](https://github.com/FasbyTech)*
