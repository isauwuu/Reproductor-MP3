# Reproductor MP3 — TPI Algoritmos y Estructuras de Datos

Reproductor de música con temática de **Máquina Expendedora Japonesa** :) . Proyecto integrador para la materia AyED — implementa estructuras de datos propias en lugar de las de Java, como parte de los requisitos de la materia.
---

## => ¿Qué hace?

Carga archivos MP3, lee sus metadatos (título, artista, portada del álbum) y los reproduce. La lista de canciones está manejada con estructuras propias — nada de `ArrayList`, ni de `LinkedList` de Java. Todo construido desde cero como pide la materia.

La interfaz en si tiene estética Lo-Fi, que encaja bastante bien con la temática japonesa que elegimos.

##  => Estructuras implementadas

- **Lista Simple (doblemente enlazada)** — para el manejo general de canciones
- **Lista Ordenada (doblemente enlazada)** — para mantener el orden por distintos criterios

## Stack

| Tecnología -- Versión |

- Java — JDK 24
- JavaFX — 21
- mp3agic — 0.9.1
  
## Cómo ejecutarlo

Se necesita el JDK 24 y el SDK de JavaFX 21. Recomendamos IntelliJ — con otro IDE hay que configurar JavaFX a mano.

1. Clonar el repo
2. En IntelliJ: `File → Project Structure → Libraries` y agregar el SDK de JavaFX 21
3. Agregar `lib/mp3agic-0.9.1.jar` al Build Path en el mismo lugar
4. Correr la clase `Main`

Si no levanta, casi seguro es JavaFX. Verificar que los VM options del run configuration tengan:
--module-path /ruta/a/javafx/lib --add-modules javafx.controls,javafx.fxml,javafx.media

## Estructura del proyecto

```
/src
  /modelo       → estructuras de datos y lógica
  /vista        → FXML y controladores de UI  
  /controlador  → conexión entre modelo y vista
/lib
  mp3agic-0.9.1.jar
README.md
```
---
Hecho para regularizar AyED — 2026.
