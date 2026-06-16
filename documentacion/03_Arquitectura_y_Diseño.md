# Arquitectura de Software y Diseño de UI

## 1. Patrón Arquitectónico: MVVM (Model-View-ViewModel)
La app se divide en capas siguiendo los principios de **Clean Architecture**:
- **Capa de UI (Compose)**: Pantallas reactivas que observan el estado.
- **Capa de Dominio (Use Cases)**: Lógica de negocio pura (ej: `SearchVolumesUseCase`).
- **Capa de Datos (Repository)**: Gestión de fuentes de datos (Local vs Remote).

## 2. Flujo de Datos Reactivo
Se ha implementado **StateFlow** y **SharedFlow** para garantizar que la interfaz de usuario se actualice en tiempo real sin bloqueos, especialmente crítico durante el reconocimiento de voz y el escaneo por cámara.

## 3. Experiencia de Usuario (Material Design 3)
- **Splash Screen**: Animación de entrada de 2.5s para reforzar la identidad de marca.
- **Feedback Háptico y Visual**: Barras de progreso inteligentes durante el escaneo de ISBN (confirmación de 3s).
- **Adaptabilidad**: Soporte para temas oscuros y gestión de estados de error mediante diálogos amigables.
