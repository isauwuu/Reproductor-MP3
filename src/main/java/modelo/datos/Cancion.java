package modelo.datos;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * Representa una canción cargada desde un archivo MP3 en el sistema de archivos.
 * Contiene metadatos de la canción como título, artista, año, duración y la portada.
 */
public class Cancion {

    private String titulo;
    private String artista;
    private int anio;
    private String rutaArchivo;
    private Image portada;
    private boolean valida;
    private long duracionSegundos;

    /**
     * Crea una nueva canción a partir de la ruta del archivo MP3
     * 
     * @param rutaArchivo Ruta absoluta del archivo MP3 en el disco.
     */
    public Cancion(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        this.titulo = "Desconocido";
        this.artista = "Desconocido";
        this.anio = -1;
        this.duracionSegundos = 0;
        this.portada = null;
        this.valida = false;
        cargaMetadatos();
    }

    /**
     * Carga y procesa los metadatos ID3v1 o ID3v2 del archivo MP3 usando la biblioteca mp3agic.
     */
    private void cargaMetadatos() {
        try {
            File archivo = new File(this.rutaArchivo);
            if (!archivo.exists()) return;
            this.titulo = archivo.getName().replaceAll("(?i)\\.mp3$", "");

            Mp3File mp3 = new Mp3File(this.rutaArchivo);
            this.duracionSegundos = mp3.getLengthInSeconds();

            if (mp3.hasId3v2Tag()) {
                ID3v2 tag = mp3.getId3v2Tag();
                asignarMetadatosBasicos(tag.getTitle(), tag.getArtist(), tag.getYear());
                byte[] portadaBytes = tag.getAlbumImage();
                if (portadaBytes != null)
                    this.portada = new Image(new ByteArrayInputStream(portadaBytes));

            } else if (mp3.hasId3v1Tag()) {
                ID3v1 tag = mp3.getId3v1Tag();
                asignarMetadatosBasicos(tag.getTitle(), tag.getArtist(), tag.getYear());
            }
            this.valida = true;
        } catch (Exception e) {}
    }

    /**
     * Asigna título, artista y año si son válidos.
     * 
     * @param titulo  Título de la pista.
     * @param artista Nombre del artista.
     * @param yearStr Cadena representando el año.
     */
    private void asignarMetadatosBasicos(String titulo, String artista, String yearStr) {
        if (titulo != null && !titulo.trim().isEmpty())
            this.titulo = titulo.trim();

        if (artista != null && !artista.trim().isEmpty())
            this.artista = artista.trim();

        this.anio = parsearAnio(yearStr);
    }

    /**
     * Convierte la cadena del año a entero.
     * 
     * @param yearStr Cadena del año.
     * @return El año como entero, o -1 si es inválido.
     */
    private int parsearAnio(String yearStr) {
        if (yearStr == null || yearStr.trim().isEmpty()) return -1;
        try {
            return Integer.parseInt(yearStr.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Obtiene si la canción ha sido cargada con metadatos válidos.
     * 
     * @return true si es válida.
     */
    public boolean isValida(){
        return valida;
    }

    /**
     * Obtiene la imagen de la portada de la canción (si existe).
     * 
     * @return Objeto Image o null.
     */
    public Image getPortada(){
        return portada;
    }

    /**
     * Obtiene el año de lanzamiento de la canción.
     * 
     * @return Año de la canción.
     */
    public int getAnio(){
        return anio;
    }

    /**
     * Obtiene el artista/banda de la canción.
     * 
     * @return Nombre del artista.
     */
    public String getArtista(){
        return artista;
    }

    /**
     * Obtiene el título de la canción.
     * 
     * @return Título de la pista.
     */
    public String getTitulo(){
        return titulo;
    }

    /**
     * Obtiene la URI para reproducción multimedia de JavaFX.
     * 
     * @return URI en formato string.
     */
    public String getMediaURI() {
        return new File(rutaArchivo).toURI().toString();
    }

    /**
     * Obtiene la ruta "path" del archivo de la cancion
     * @return path de la cancion
     */
    public String getRutaArchivo() {
        return rutaArchivo;
    }

    /**
     * Representación textual de la canción (usada por la lista de eliminación).
     * 
     * @return Cadena con el título y artista.
     */
    @Override
    public String toString() {
        return titulo + " - " + artista;
    }

    /**
     * Compara esta canción con otro objeto para verificar la igualdad.
     * Dos canciones se consideran iguales si tienen el mismo título y artista.
     * 
     * @param o Objeto a comparar con esta canción.
     * @return true si el objeto es igual a esta canción, false en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cancion)) return false;
        Cancion other = (Cancion) o;
        return titulo.equals(((Cancion) o).getTitulo()) && artista.equals(((Cancion) o).getArtista());
    }
}