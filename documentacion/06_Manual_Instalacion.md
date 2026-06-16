# Manual de Instalación y Despliegue

## 1. Requisitos Previos
- **Android Studio Ladybug** o superior.
- **JDK 17** (configurado en el proyecto).
- Un dispositivo Android físico o emulador con **API 24** (Android 7.0) como mínimo.
- Conexión a Internet activa.

## 2. Configuración de API Keys
Para que las funcionalidades inteligentes operen, es necesario crear un archivo `local.properties` en la raíz del proyecto con el siguiente contenido:

```properties
google.books.api.key=TU_GOOGLE_BOOKS_KEY
gemini.api.key=TU_GEMINI_AI_KEY
```

## 3. Despliegue
1. Clonar el repositorio.
2. Sincronizar Gradle (`Sync Project with Gradle Files`).
3. Asegurarse de que el certificado SHA-1 de su entorno de desarrollo esté registrado en la Google Cloud Console para la clave de Google Books.
4. Ejecutar la tarea `:app:assembleDebug` o pulsar el botón **Run** en Android Studio.

## 4. Notas del Desarrollador
El proyecto utiliza **KSP2**, por lo que la primera compilación puede tardar ligeramente más mientras se generan los archivos de Room y Hilt.
