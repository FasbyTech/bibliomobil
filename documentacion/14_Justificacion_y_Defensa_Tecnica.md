# Defensa del Proyecto: Justificación de Decisiones Técnicas

Este documento detalla el razonamiento de ingeniería detrás de las decisiones clave tomadas durante el desarrollo de **BiblioMobil**, proporcionando argumentos sólidos para la defensa ante el tribunal.

## 1. Arquitectura: Clean Architecture + MVVM
**Decisión**: Separar el proyecto en capas de UI, Domain y Data.
- **Justificación**: Se rechazó el modelo MVC/MVP tradicional para evitar el "Acoplamiento Masivo". Clean Architecture garantiza que la lógica de negocio (casos de uso como el cálculo de similitud) sea independiente de si los datos vienen de una base de datos local o de una API externa.
- **Ventaja**: Permite cambiar el motor de IA (ej. cambiar Gemini por OpenAI) o el motor de búsqueda sin tocar una sola línea de la interfaz de usuario.

## 2. UI Framework: Jetpack Compose y MD3
**Decisión**: Uso exclusivo de UI declarativa y Material Design 3.
- **Justificación**: Se optó por Compose sobre los XML tradicionales para reducir el "Boilerplate" y aprovechar la potencia de las animaciones nativas. MD3 se eligió para implementar **Color Dinámico**, permitiendo que la app se adapte al sistema operativo del usuario, una característica clave de las apps modernas de Android.

## 3. Estrategia de IA y Visión Artificial
**Decisión**: Google Gemini 1.5 Flash + ML Kit.
- **Justificación**: Se eligió el modelo **Flash** por su baja latencia, crucial para dispositivos móviles. En cuanto al OCR, se decidió usar **ML Kit (on-device)** en lugar de soluciones en la nube para garantizar que el escaneo funcione sin retrasos de red y proteger la privacidad de la cámara del usuario.

## 4. UX: El "Escaneo con Confirmación" de 3s
**Decisión**: Implementar un temporizador de 3 segundos antes de navegar tras detectar un ISBN.
- **Justificación**: Las pruebas de usuario demostraron que el OCR es tan rápido que a veces detectaba códigos parciales o libros erróneos al mover la cámara. El delay de 3 segundos con feedback visual (barra de progreso) otorga **control y seguridad** al usuario, eliminando navegaciones accidentales.

## 5. Rendimiento: Algoritmo de Levenshtein Optimizado
**Decisión**: Rediseño del algoritmo de búsqueda difusa para usar memoria O(n).
- **Justificación**: Una implementación estándar de Levenshtein consume mucha RAM en dispositivos móviles al comparar sinopsis largas. Mi decisión de optimizarlo a solo dos filas en memoria garantiza que la app no sufra "crashes" por OutOfMemory en dispositivos de gama entrada.

## 6. Seguridad: Restricción de APIs mediante SHA-1
**Decisión**: Inyectar headers de firma en el NetworkModule.
- **Justificación**: Exponer una API Key en una app Android es un riesgo. Al implementar la restricción por certificado SHA-1 y nombre de paquete, se garantiza que aunque alguien extraiga la Key del APK, esta sea **inutilizable** fuera de este entorno oficial.
