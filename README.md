# BiblioMobil - Gestión Inteligente de Bibliotecas Personales

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.0-blue.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**BiblioMobil** es una aplicación Android de vanguardia diseñada para la catalogación y gestión eficiente de bibliotecas personales de libros y cómics. Desarrollada como proyecto final (TFM) para el **Máster en Desarrollo con IA** de **Big School**.

---

## 🌟 Funcionalidades Principales

- **🔍 Escáner OCR Inteligente**: Digitaliza libros en segundos escaneando su código ISBN con confirmación visual de estabilidad.
- **🎙️ Búsqueda por Voz**: Acceso total al catálogo mediante comandos de voz naturales.
- **🤖 IA Generativa (Gemini 1.5 Flash)**: Generación automática de sinopsis y enriquecimiento de metadatos.
- **📝 Gestión de Préstamos**: Trazabilidad completa de quién tiene tus libros y cuándo deben volver.
- **⭐ Valoración y Reseñas**: Sistema de estrellas interactivo y espacio para tu opinión personal.
- **📁 Soberanía de Datos**: Backup local/cloud (Google Drive) y exportación a CSV.
- **🖼️ Editor de Portadas**: Herramienta de recorte (crop) integrada para portadas personalizadas.
- **🌓 Diseño Profesional**: Interfaz moderna (Material 3) con soporte para Modo Oscuro y Color Dinámico.

---

## 🛠️ Stack Tecnológico (MAD)

- **Lenguaje**: Kotlin 2.4.0
- **Arquitectura**: Clean Architecture + MVVM + Inyección de Dependencias (Hilt)
- **UI**: Jetpack Compose con Material Design 3
- **Persistencia**: Room Database con soporte FTS4/5
- **IA**: Google Gemini AI & ML Kit (On-device OCR)
- **Networking**: Retrofit 2 + OkHttp 5
- **Hardware**: CameraX & Speech Recognizer API

---

## 📂 Documentación del Proyecto

El proyecto incluye un dossier académico completo en la carpeta [`/documentacion`](./documentacion):

1.  **[01_Resumen_Ejecutivo](./documentacion/01_Resumen_Ejecutivo.md)**
2.  **[04_Funcionalidades_Tecnicas](./documentacion/04_Funcionalidades_Tecnicas.md)**
3.  **[10_Ficha_Tecnica](./documentacion/10_Ficha_Tecnica.md)**
4.  **[14_Justificacion_y_Defensa_Tecnica](./documentacion/14_Justificacion_y_Defensa_Tecnica.md)**
5.  **[Manual de Usuario](./documentacion/07_Manual_Usuario.md)**
6.  *...y 10 documentos más.*

---

## 🚀 Instalación y Despliegue

1. Clona el repositorio.
2. Añade tus API Keys en `local.properties`:
   ```properties
   google.books.api.key=TU_KEY
   gemini.api.key=TU_KEY
   ```
3. Registra tu firma SHA-1 en Google Cloud Console.
4. Ejecuta en Android Studio.

---

**Desarrollado por FasbyTech**  
*TFM - Máster en Desarrollo con IA (Big School)*
