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
