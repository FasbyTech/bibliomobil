# Guía de Despliegue e Instalación

## Requisitos
- Android API 24 o superior.
- Android Studio Ladybug+
- Google Cloud Console Project con **Books API** y **Generative AI API** habilitadas.

## Instalación
1. Clonar: `git clone https://github.com/FasbyTech/bibliomobil.git`
2. Configurar `local.properties`:
   ```properties
   google.books.api.key=TU_KEY
   gemini.api.key=TU_KEY
   ```
3. Sincronizar Gradle y ejecutar en el dispositivo.
