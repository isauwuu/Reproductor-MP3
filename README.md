# [ ♫ ] Reproductor MP3 — TPI Algoritmos y Estructuras de Datos
 
Aplicación de escritorio en Java para reproducir archivos MP3, hecha como Trabajo Práctico Integrador de la materia Algoritmos y Estructuras de Datos. Usa estructuras de datos propias en lugar de las que ya trae Java, por requisito de la materia.
 
---
 
## [ * ] Características
 
Reproduce archivos MP3 y lee sus metadatos (título, artista, año y portada del álbum). La organización y el ordenamiento de canciones se maneja con estructuras personalizadas. La interfaz está desarrollada con JavaFX y tiene estética Lo-Fi inspirada en máquinas expendedoras japonesas.
 
---
 
## [ # ] Estructuras de datos implementadas
 
Sin `ArrayList` ni `LinkedList` de Java — todo construido desde cero: lista doblemente enlazada, lista ordenada doblemente enlazada, nodos personalizados y criterios de ordenamiento dinámicos.
 
---
 # [ √ ] Objetivo académico

Este proyecto fue desarrollado para practicar los conceptos vistos en la materia: estructuras enlazadas, programación orientada a objetos, manipulación de archivos MP3, interfaces gráficas con JavaFX, arquitectura MVC y manejo de dependencias con Maven.

---
## [~] Tecnologías utilizadas
 
| Tecnología | Versión |
|---|---|
| Java | JDK 21+ |
| JavaFX | 22 |
| Maven | 3.9+ |
| mp3agic | 0.9.1 |
 
---
 
## [/] Estructura del proyecto
 
```
src/
├── main/
│   ├── java/
│   │   ├── modelo/
│   │   │   ├── criterios/
│   │   │   ├── datos/
│   │   │   ├── estructuras/
│   │   │   └── interfaces/
│   │   └── reproductor/
│   │       └── Main.java
│   └── resources/
│       └── views/
│           └── main-view.fxml
```
 
---
 
## [>] Cómo ejecutar el proyecto
 
**IntelliJ (recomendado):** clonar el repo, abrirlo en IntelliJ y esperar a que Maven descargue las dependencias. Después correr `reproductor.Main` o usar el botón Run.
 
```bash
git clone https://github.com/isauwuu/Reproductor-MP3.git
cd Reproductor-MP3
```
 
**Desde terminal:** requiere JDK 21+ y Maven instalados (`java -version` y `mvn -version` para verificar).
 
```bash
mvn clean javafx:run
```
 
Las dependencias (JavaFX, mp3agic) se descargan automáticamente con Maven.
 
---
 
## [!] Problemas comunes
 
**"JavaFX runtime components are missing"** — casi siempre es porque no se abrió como proyecto Maven. Cerrar, volver a abrir eligiendo Maven, esperar que cargue y correr `mvn clean install`.
 
**"Location is not set"** — verificar que exista `src/main/resources/views/main-view.fxml` y que se esté cargando correctamente desde `Main.java`.
 
---
 
Proyecto realizado para la materia **Algoritmos y Estructuras de Datos** — UNSa 2026.

Desarrollado por estudiantes de la UNSa.
