package modelo.datos;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.io.File;

public class Cancion {

    private String titulo;
    private String artista;
    private int anio;
    private String rutaArchivo;
    private Image portada;
    private boolean valida;
    private long duracionSegundos;
    private int posEnLista;

    public Cancion(String rutaArchivo, int posEnLista) {
        this.rutaArchivo = rutaArchivo;
        this.titulo = "Desconocido";
        this.artista = "Desconocido";
        this.anio = -1;
        this.duracionSegundos = 0;
        this.portada = null;
        this.valida = false;
        this.posEnLista = posEnLista;
        cargaMetadatos();
    }

    private void cargaMetadatos() {
        try {
            File archivo = new File(this.rutaArchivo);
            if (!archivo.exists()) return;

            // Si no hay metadatos, el nombre del archivo es mejor que "Desconocido"
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
                // ID3v1 no incluye portada
            }

            this.valida = true;

        } catch (Exception e) {
            // valida queda en false; MainController se encarga de avisar a la UI
        }
    }

    private void asignarMetadatosBasicos(String titulo, String artista, String yearStr) {
        if (titulo != null && !titulo.trim().isEmpty())
            this.titulo = titulo.trim();

        if (artista != null && !artista.trim().isEmpty())
            this.artista = artista.trim();

        this.anio = parsearAnio(yearStr);
    }

    private int parsearAnio(String yearStr) {
        if (yearStr == null || yearStr.trim().isEmpty()) return -1;
        try {
            return Integer.parseInt(yearStr.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Getters
    public boolean isValida(){
        return valida;
    }
    public Image getPortada(){
        return portada;
    }
    public int getAnio(){
        return anio;
    }
    public String getArtista(){
        return artista;
    }
    public String getTitulo(){
        return titulo;
    }

    public String getMediaURI() {
        return new File(rutaArchivo).toURI().toString();
    }

    @Override
    public String toString() {
        return "Cancion{" +
                "titulo='" + titulo + '\'' +
                ", artista='" + artista + '\'' +
                ", anio=" + anio +
                ", rutaArchivo='" + rutaArchivo + '\'' +
                ", valida=" + valida +
                '}';
    }
}