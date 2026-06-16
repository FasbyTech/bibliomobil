# Política de Privacidad - BiblioMobil

**Última actualización: 16 de junio de 2026**

En **BiblioMobil** (desarrollado por FasbyTech), nos tomamos muy en serio la privacidad de nuestros usuarios. Esta política describe cómo manejamos la información dentro de la aplicación.

## 1. Información que Recopilamos
BiblioMobil es una herramienta de gestión de bibliotecas personales que funciona principalmente de forma local. No requerimos la creación de una cuenta de usuario ni recopilamos datos personales identificables (como nombre, correo electrónico o ubicación).

## 2. Uso de Permisos Críticos
Para ofrecer sus funcionalidades inteligentes, la aplicación solicita los siguientes permisos:
- **Cámara**: Utilizada exclusivamente para el escaneo de códigos ISBN y la captura de portadas de libros. Las imágenes procesadas para el OCR se analizan en tiempo real y no se almacenan en servidores externos.
- **Micrófono (Grabación de Audio)**: Utilizado únicamente cuando el usuario activa la búsqueda por voz. El audio se procesa para convertirlo a texto y no se almacena de ninguna forma.
- **Acceso a Internet**: Necesario para consultar la API de Google Books (metadatos de libros) y Google Gemini AI (generación de resúmenes).

## 3. Procesamiento de Datos por Terceros
La aplicación se comunica con servicios de Google para enriquecer la experiencia:
- **Google Books API**: Se envían consultas de búsqueda (títulos o ISBNs) para recuperar información bibliográfica.
- **Google Gemini AI**: Se envía el título y/o sinopsis de los libros para generar resúmenes automáticos. 
*Nota: Estos servicios se rigen por las políticas de privacidad de Google.*

## 4. Almacenamiento Local
Toda tu biblioteca (títulos, autores, portadas guardadas) se almacena localmente en tu dispositivo mediante una base de datos SQLite (Room). Si desinstalas la aplicación, estos datos se eliminarán a menos que realices una copia de seguridad manual del sistema.

## 5. Seguridad
Implementamos las mejores prácticas de seguridad en Android, incluyendo la restricción de claves de API y el uso de conexiones cifradas (HTTPS) para todas las comunicaciones externas.

## 6. Cambios en esta Política
Podemos actualizar nuestra Política de Privacidad de vez en cuando. Se recomienda revisar esta página periódicamente para ver cualquier cambio.

## 7. Contacto
Si tienes alguna pregunta sobre esta Política de Privacidad, puedes contactarnos a través del repositorio oficial en GitHub.
