# Reproductor MP3 — TPI Algoritmos y Estructuras de Datos

Aplicación de escritorio desarrollada en Java para reproducción de archivos MP3, creada como Trabajo Práctico Integrador de la materia **Algoritmos y Estructuras de Datos**.

El proyecto implementa estructuras de datos propias desde cero, evitando el uso de colecciones provistas por Java como parte de los requisitos académicos.

---

## 🎵 Características

- Reproducción de archivos MP3
- Lectura de metadatos:
  - título
  - artista
  - año
  - portada del álbum
- Interfaz gráfica desarrollada con JavaFX
- Organización y ordenamiento de canciones mediante estructuras personalizadas
- Estética visual inspirada en máquinas expendedoras japonesas y estilo Lo-Fi

---

## Estructuras de datos implementadas

El proyecto evita utilizar estructuras de Java como `ArrayList` o `LinkedList`.

Se implementaron manualmente:

- Lista doblemente enlazada
- Lista ordenada doblemente enlazada
- Nodos personalizados
- Criterios de ordenamiento dinámicos

---

## Tecnologías utilizadas

| Tecnología | Versión |
|---|---|
| Java | JDK 21+ |
| JavaFX | 22 |
| Maven | 3.9+ |
| mp3agic | 0.9.1 |

---

## 📁 Estructura del proyecto

```text
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
│   │
│   └── resources/
│       └── views/
│           └── main-view.fxml
│
└── resources/
```

---

# Cómo ejecutar el proyecto

## Opción 1 — IntelliJ IDEA (recomendada)

### Requisitos

- JDK 21 o superior
- IntelliJ IDEA
- Plugin de JavaFX habilitado

### Pasos

1. Clonar el repositorio

```bash
git clone <URL_DEL_REPO>
cd Reproductor-MP3
```

2. Abrir el proyecto en IntelliJ IDEA

3. Esperar a que Maven descargue las dependencias automáticamente

4. Ejecutar la clase:

```text
reproductor.Main
```

o simplemente usar el botón ▶ Run del IDE.

---

## Opción 2 — Desde terminal con Maven

### Requisitos

Instalar:

- Java JDK 21+
- Maven

Verificar instalación:

```bash
java -version
mvn -version
```

### Ejecutar

```bash
mvn clean javafx:run
```

---

# 📦 Dependencias

El proyecto usa Maven, por lo que las dependencias se descargan automáticamente.

Principales librerías:

- JavaFX
- mp3agic

---

# ⚠️ Problemas comunes

## Error:

```text
JavaFX runtime components are missing
```

### Solución

Asegurarse de:

- usar JDK 21+
- abrir el proyecto como proyecto Maven
- esperar a que IntelliJ importe las dependencias
- ejecutar `mvn clean install`

---

## Error:

```text
Location is not set
```

### Solución

Verificar que exista el archivo:

```text
src/main/resources/views/main-view.fxml
```

y que se cargue correctamente desde `Main.java`.

---

# Objetivo académico

Este proyecto fue desarrollado para practicar:

- estructuras enlazadas
- programación orientada a objetos
- manipulación de archivos MP3
- interfaces gráficas con JavaFX
- arquitectura MVC
- manejo de dependencias con Maven

---

# Autores

Proyecto realizado para la materia **Algoritmos y Estructuras de Datos** — 2026.

Desarrollado por estudiantes de la UNSa.
