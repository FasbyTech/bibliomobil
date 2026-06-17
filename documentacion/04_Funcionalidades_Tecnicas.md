# Análisis de Funcionalidades Críticas

## 1. Algoritmo de Escaneo de ISBN
Se utiliza un motor de análisis de frames que:
1. Captura texto bruto mediante **ML Kit**.
2. Filtra mediante una expresión regular robusta para detectar ISBN-10 y ISBN-13.
3. Implementa una **ventana de confirmación de 3 segundos** para validar la estabilidad del código antes de disparar la navegación.

## 2. Búsqueda por Voz y Texto
- **Voice Recognition**: Integración con el motor nativo de Android.
- **SQLite LIKE Optimization**: Búsqueda flexible que permite encontrar libros por fragmentos de su título o palabras clave contenidas en su sinopsis.

## 3. Integración con Gemini AI
La app actúa como un puente entre la biblioteca del usuario y la IA de Google:
- **Resúmenes**: Si un libro no tiene descripción, Gemini la genera basándose en el título.
- **Mejora**: Permite refinar sinopsis existentes para hacerlas más concisas o atractivas.

## 4. Sistema de Backup y Restauración
BiblioMobil garantiza la seguridad de la biblioteca personal mediante:
- **Copia de Seguridad**: Exportación completa de la base de datos SQLite.
- **Integración con Nube**: Opción de compartir el archivo de backup directamente a Google Drive mediante el Share Sheet de Android.
- **Restauración**: Motor de importación que valida y reemplaza la base de datos activa tras un reinicio de seguridad.

## 5. Exportación de Informes (CSV)
Para facilitar la interoperabilidad con otras herramientas (Excel, Google Sheets):
- **Generación Dinámica**: El sistema recorre la base de datos y genera una cadena CSV estructurada con ISBN, títulos, autores y estado de lectura.
- **Portabilidad**: Integración con el sistema de intercambio de archivos nativo de Android.
