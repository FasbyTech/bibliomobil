# BiblioMobil - Gestión Inteligente de Bibliotecas Personales

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.0-blue.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**BiblioMobil** es una aplicación Android de vanguardia diseñada para la catalogación y gestión eficiente de bibliotecas personales de libros y cómics. Desarrollada por **Guillermo J. Conesa** como proyecto final (TFM) para el **Máster en Desarrollo con IA** de **Big School**.

---

## 🌟 Funcionalidades Principales

- **🔍 Escáner OCR Inteligente**: Digitaliza libros en segundos escaneando su código ISBN con confirmación visual de estabilidad (delay de 3s para precisión).
- **🎙️ Búsqueda por Voz**: Acceso total al catálogo mediante comandos de voz naturales.
- **🤖 IA Generativa (Gemini 1.5 Flash)**: Generación automática de sinopsis y enriquecimiento de metadatos literarios.
- **📝 Gestión de Préstamos**: Trazabilidad completa (Relaciones 1:N) de quién tiene tus libros y cuándo deben volver.
- **⭐ Valoración y Reseñas**: Sistema de estrellas interactivo y espacio para críticas literarias personales.
- **📁 Soberanía de Datos**: Backup local/cloud (Google Drive), restauración atómica y exportación a CSV mediante SAF.
- **🖼️ Editor de Portadas**: Herramienta de recorte (crop) integrada para un acabado visual impecable.
- **🌓 Diseño Profesional**: Interfaz moderna (Material 3) con soporte para Modo Oscuro y Color Dinámico.

---

## 🛠️ Stack Tecnológico (MAD)

- **Lenguaje**: Kotlin 2.4.0 (Coroutines & Flow)
- **Arquitectura**: Clean Architecture + MVVM + Inyección de Dependencias (Hilt)
- **UI**: Jetpack Compose con Material Design 3
- **Persistencia**: Room Database con soporte FTS4/5 y modo WAL
- **IA**: Google Gemini AI & ML Kit (On-device OCR)
- **Networking**: Retrofit 2 + OkHttp 5 (Interceptores de Seguridad SHA-1)
- **Hardware**: CameraX & Speech Recognizer API

---

## 📂 Documentación del Proyecto

El proyecto incluye un dossier académico consolidado en la carpeta [`/documentacion`](./documentacion):

1.  **[01_Memoria_Tecnica.md](./documentacion/01_Memoria_Tecnica.md)**: Resumen, Stack, Arquitectura y Pruebas.
2.  **[02_Manual_de_Usuario.md](./documentacion/02_Manual_de_Usuario.md)**: Guía paso a paso para el usuario final.
3.  **[03_Despliegue_e_Instalacion.md](./documentacion/03_Despliegue_e_Instalacion.md)**: Instrucciones de compilación y API Keys.
4.  **[04_Anexo_Codigo_Fuente.md](./documentacion/04_Anexo_Codigo_Fuente.md)**: Recopilación de las clases core comentadas.
5.  **[05_Politica_de_Privacidad.md](./documentacion/05_Politica_de_Privacidad.md)**: Transparencia en el uso de datos.

---

## 🚀 Instalación y Despliegue

1. Clona el repositorio: `git clone https://github.com/FasbyTech/bibliomobil.git`
2. Añade tus API Keys en `local.properties`:
   ```properties
   google.books.api.key=TU_KEY
   gemini.api.key=TU_KEY
   ```
3. Registra tu firma **SHA-1** en Google Cloud Console para habilitar las restricciones de seguridad.
4. Sincroniza Gradle y ejecuta en un dispositivo (API 24+).

---

**Desarrollado por Guillermo J. Conesa**  
*TFM - Máster en Desarrollo con IA (Big School)*
