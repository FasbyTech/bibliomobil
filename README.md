# BiblioMobil - Gestión Inteligente de Bibliotecas Personales

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.0-blue.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Proyecto final (TFM) para el **Máster en Desarrollo con IA** de **Big School**.

---

# BiblioMobil - Gestión Inteligente de Bibliotecas Personales

BiblioMobil es una aplicación Android de vanguardia diseñada para la catalogación y gestión eficiente de bibliotecas personales de libros y cómics. Desarrollada como proyecto de fin de máster (TFM), la aplicación combina técnicas avanzadas de Inteligencia Artificial en el dispositivo y servicios en la nube para ofrecer una experiencia fluida de digitalización y organización de colecciones literarias.

---

## 📝 Descripción General

BiblioMobil nace con el propósito de resolver la tediosa tarea de catalogar colecciones extensas de libros y cómics de forma manual. Mediante el uso de tecnologías de visión computacional y procesamiento de lenguaje natural, los usuarios pueden transformar su biblioteca física en un catálogo digital enriquecido en cuestión de segundos.

El sistema optimiza la gestión del inventario bibliográfico, permitiendo realizar búsquedas avanzadas, gestionar de manera estricta el historial de préstamos a terceros para evitar pérdidas, y personalizar la información de cada ejemplar mediante un editor de portadas nativo. Además, se prioriza la soberanía y la privacidad de los datos del usuario mediante opciones híbridas de almacenamiento local y copias de seguridad automatizadas en la nube.

---

## 🛠️ Stack Tecnológico

El desarrollo de BiblioMobil sigue estrictamente las directrices de **Modern Android Development (MAD)**, garantizando una base de código robusta, mantenible y escalable bajo una estructura de **Clean Architecture** estructurada en capas y el patrón arquitectónico **MVVM (Model-View-ViewModel)**.

*   **Lenguaje:** Kotlin 2.4.0 (aprovechando al máximo Coroutines y Flow para la programación asíncrona reactiva).
*   **Interfaz de Usuario:** Jetpack Compose con soporte completo para **Material Design 3**, incluyendo *Modo Oscuro* nativo y *Color Dinámico* (Material You).
*   **Arquitectura e Inyección de Dependencias:** Clean Architecture estructurada en tres capas (*Data*, *Domain*, *Presentation*) con **Hilt** (Dagger) para la gestión del ciclo de vida de los componentes.
*   **Persistencia Local:** Room Database con optimización de búsquedas de texto mediante soporte **FTS4/5 (Full-Text Search)**.
*   **Inteligencia Artificial y Visión:**
   *   **Google Gemini AI (Gemini 1.5 Flash):** Procesamiento en la nube para la generación automática de sinopsis descriptivas y enriquecimiento avanzado de metadatos literarios.
   *   **ML Kit (On-device OCR):** Reconocimiento de texto local de alta velocidad para la captura de códigos ISBN y textos de portadas.
*   **Networking:** Retrofit 2 combinado con OkHttp 5 para la comunicación con APIs externas y gestión eficiente de peticiones web.
*   **Hardware e Integración con el Sistema:**
   *   **CameraX API:** Control avanzado y optimizado de la cámara del dispositivo para el escáner de códigos de barras.
   *   **Speech Recognizer API:** Conversión de voz a texto local para la ejecución de comandos y búsquedas por voz.

---

## 🚀 Instalación y Ejecución

Sigue estos pasos para clonar, configurar y ejecutar el proyecto en tu entorno de desarrollo local:

### 1. Requisitos Previos
*   Android Studio Jellyfish (o superior) instalado.
*   Android SDK configurado con el nivel de API mínimo 26 (Android 8.0 Oreo).
*   Una cuenta activa de Google Cloud Console para la obtención de credenciales de servicios.

### 2. Clonar el Repositorio
```bash
git clone [https://github.com/tu-usuario/bibliomobil.git](https://github.com/tu-usuario/bibliomobil.git)
cd bibliomobil
```

### 3. Configuración de Variables de Entorno y API Keys
Crea o edita el archivo `local.properties` en la raíz del proyecto para incluir tus claves de acceso privadas:

```properties
google.books.api.key=TU_GOOGLE_BOOKS_API_KEY_AQUI
gemini.api.key=TU_GEMINI_API_KEY_AQUI
```

### 4. Configuración del Entorno Cloud (OAuth 2.0 y Backup en Drive)
Para habilitar las funciones de copia de seguridad en Google Drive y el reconocimiento inteligente:
1. Accede a [Google Cloud Console](https://console.cloud.google.com/).
2. Crea un proyecto nuevo o selecciona uno existente.
3. Genera una credencial de tipo **ID de cliente de OAuth**.
4. Registra la firma digital **SHA-1** de tu entorno de desarrollo local (puedes obtenerla ejecutando la tarea Gradle `signingReport`).

### 5. Compilación y Ejecución
1. Abre el proyecto en **Android Studio**.
2. Deja que Gradle sincronice todas las dependencias (`Sync Project with Gradle Files`).
3. Conecta un dispositivo físico o inicia un emulador Android (API 26+).
4. Haz clic en el botón **Run ('app')** o presiona `Shift + F10`.

---

## 📂 Estructuración del Proyecto

El proyecto se organiza bajo los principios de **Clean Architecture**, aislando la lógica de negocio de los detalles de implementación de la plataforma:

```text
app/
└── src/
    └── main/
        └── java/com/fasbytech/bibliomobil/
            ├── data/                 # Capa de Datos: Repositorios, DAOs, DTOs y APIs de red
            │   ├── local/            # Base de datos Room, Entidades FTS, SharedPreferences
            │   ├── remote/           # Conexión con Gemini API, Google Books API y Retrofit
            │   └── repository/       # Implementación concreta de las interfaces del dominio
            │
            ├── domain/               # Capa de Dominio (Pura de Kotlin): Lógica de Negocio
            │   ├── model/            # Modelos de datos de negocio (Libro, Prestamo, Usuario)
            │   ├── repository/       # Interfaces de los repositorios (Inversión de Dependencias)
            │   └── usecase/          # Casos de uso atómicos (ScanIsbnUseCase, GetSynopsisUseCase...)
            │
            ├── presentation/         # Capa de Presentación: UI y Estados de Vista
            │   ├── ui/               # Componentes, Temas y Pantallas de Jetpack Compose
            │   ├── viewmodel/        # ViewModels encargados de retener y manejar el estado de la UI
            │   └── navigation/       # Configuración de rutas y flujo de navegación de la app
            │
            └── di/                   # Módulos de Inyección de Dependencias con Hilt
```

> 📔 **Nota Académica:** El repositorio incluye un dossier académico detallado con la justificación técnica del proyecto en la ruta `/documentacion`, compuesto por archivos clave como `01_Resumen_Ejecutivo.pdf`, `04_Funcionalidades_Tecnicas.pdf`, `10_Ficha_Tecnica.pdf`, `14_Justificacion_y_Defensa_Tecnica.pdf` y el `Manual de Usuario`.

---

## ✨ Funcionalidades Detalladas

*   **🔍 Escáner OCR Inteligente:** Captura rápida e inalámbrica de libros a través del código ISBN utilizando la cámara trasera. Incorpora un algoritmo de confirmación visual de estabilidad para evitar capturas borrosas o erróneas antes de lanzar la consulta.
*   **🎙️ Búsqueda por Voz Avanzada:** Permite buscar autores, títulos o géneros completos mediante comandos de voz naturales procesados de manera local, optimizando la accesibilidad.
*   **🤖 Enriquecimiento con IA Generativa:** Al procesar un libro, **Gemini 1.5 Flash** analiza el contexto y genera automáticamente sinopsis creativas, clasifica el género y sugiere etiquetas relevantes para mantener un catálogo altamente organizado.
*   **📝 Gestión de Préstamos y Trazabilidad:** Panel de control dedicado a realizar un seguimiento minucioso de los ejemplares prestados, registrando nombres de contactos, fechas de entrega y enviando alertas visuales de devoluciones atrasadas.
*   **⭐ Valoración y Reseñas:** Sistema interactivo de puntuación con estrellas animadas y un editor de texto enriquecido para redactar críticas literarias personales.
*   **📁 Soberanía de Datos y Portabilidad:** Exportación instantánea de todo el catálogo en formato estructurado CSV. Soporte nativo para sincronizar y restaurar copias de seguridad encriptadas en la cuenta personal de Google Drive del usuario.
*   **🖼️ Editor de Portadas Integrado:** Herramienta de recorte gráfico nativa (*crop*) que permite reencuadrar imágenes capturadas con la cámara o descargadas de internet para asegurar que el aspecto visual de la estantería digital sea impecable.

---
Desarrollado por Guillermo Conesa Alfaro, 
*TFM - Máster en Desarrollo con IA II(Big School)