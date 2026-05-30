#  Reproductor MP3 — TPI Algoritmos y Estructuras de Datos

Aplicación de escritorio en Java que funciona como reproductor de archivos MP3
con estética de tocadiscos retro. Desarrollada como Trabajo Práctico Integrador
de la materia Algoritmos y Estructuras de Datos. Usa estructuras de datos propias
en lugar de las que ya trae Java, por requisito de la materia.

---

## [ ♪ ] Características

Reproduce archivos MP3 y extrae sus metadatos (título, artista, año y portada
del álbum) directamente desde los tags ID3. La interfaz imita visualmente un
tocadiscos físico vintage con vinilo animado, brazo de aguja, panel de controles
y paleta de colores que cambia dinámicamente según la portada del disco en
reproducción.

La organización y el ordenamiento de canciones se maneja con estructuras
personalizadas. Sin ArrayList ni LinkedList de Java — todo construido desde cero.

---

## [ # ] Estructuras de datos implementadas

- **Lista doblemente enlazada** — lista de reproducción principal
- **Lista ordenada doblemente enlazada** — ordenamiento dinámico de canciones
- **Nodos personalizados** (NodoDoble)
- **Criterios de ordenamiento** intercambiables: por nombre, artista y año
  (implementados con el patrón Strategy mediante interfaces propias)

---

## [ √ ] Objetivo académico

Proyecto desarrollado para practicar los conceptos vistos en la materia: estructuras enlazadas, programación orientada a objetos, manipulación de archivos MP3, interfaces gráficas con JavaFX, arquitectura MVC y manejo de dependencias con Maven.

---

## [ ~ ] Tecnologías y dependencias

| Tecnología     | Versión   | Uso                                      |
|----------------|-----------|------------------------------------------|
| Java           | JDK 21+   | Lenguaje principal                       |
| JavaFX         | 22        | Interfaz gráfica                         |
| Maven          | 3.9+      | Gestión de dependencias y build          |
| mp3agic        | 0.9.1     | Lectura de metadatos ID3 y portadas MP3  |
| Color Thief    | 1.1.2     | Extracción de paleta desde portada       |

---

## [ / ] Estructura del proyecto
```
src/
├── main/
│   ├── java/
│   │   ├── controllers/
│   │   │   └── MainController.java
│   │   ├── modelo/
│   │   │   ├── criterios/
│   │   │   │   ├── CriterioOrdenacion.java
│   │   │   │   ├── PorAnio.java
│   │   │   │   ├── PorArtista.java
│   │   │   │   └── PorNombre.java
│   │   │   ├── datos/
│   │   │   │   ├── Cancion.java
│   │   │   │   ├── ExtractorPaleta.java
│   │   │   │   ├── ListaCancion.java
│   │   │   │   ├── ListaCancionOrdenada.java
│   │   │   │   └── Paleta.java
│   │   │   ├── estructuras/
│   │   │   │   ├── Lista0DLinkedL.java
│   │   │   │   ├── Lista1DLinkedL.java
│   │   │   │   ├── Lista2DLinkedL.java
│   │   │   │   └── NodoDoble.java
│   │   │   └── interfaces/
│   │   │       ├── OperacionesCL2.java
│   │   │       ├── OperacionesCL3.java
│   │   │       └── OperacionesCL4.java
│   │   ├── reproductor/
│   │   │   └── Main.java
│   │   └── services/
│   │       └── OrdenamientoService.java
│   └── resources/
│       ├── fonts/
│       │   └── PressStart2P-Regular.ttf
│       ├── styles/
│       │   ├── controls.css
│       │   ├── player.css
│       │   ├── playlist.css
│       │   └── style.css
│       └── views/
│           └── main-view.fxml
```
---
## [ > ] Cómo ejecutar el proyecto

**IntelliJ (recomendado):** clonar el repo, abrirlo en IntelliJ y esperar a que
Maven descargue las dependencias. Después correr `reproductor.Main` o usar el
botón Run.

```bash
git clone https://github.com/isauwuu/Reproductor-MP3.git
cd Reproductor-MP3
```

**Desde terminal:** requiere JDK 21+ y Maven instalados.

```bash
mvn clean javafx:run
```

Las dependencias se descargan automáticamente con Maven al hacer el primer build.

---

## [ ! ] Problemas comunes

**"JavaFX runtime components are missing"** — casi siempre es porque no se abrió
como proyecto Maven. Cerrar, volver a abrir eligiendo Maven, esperar que cargue
y correr `mvn clean install`.

**"Location is not set"** — verificar que exista
`src/main/resources/views/main-view.fxml` y que se esté cargando correctamente
desde `Main.java`.

**El vinilo no gira** — revisar que el `AnimationTimer` esté iniciado en el
controlador y que el `Canvas` esté correctamente inyectado con `fx:id`.

---

Proyecto realizado para la materia **Algoritmos y Estructuras de Datos** — UNSa 2026.
Desarrollado por estudiantes de la UNSa.
