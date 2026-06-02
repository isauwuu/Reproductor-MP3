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

    public Cancion(String rutaArchivo,int posEnLista) {
        this.rutaArchivo = rutaArchivo;
        this.titulo = "Desconocido";
        this.artista = "Desconocido";
        this.anio = -1;
        this.duracionSegundos=0;
        this.portada = null;
        this.valida = false;
        this.posEnLista=posEnLista;
        cargaMetadatos();

    }

    private void asignarMetadatosBasicos(String titulo, String artista, int anio) {

        //.trim saca los espacios de los costados, sirve para asegurarse q el titulo no este vacio nomas "   " -> ""
        //es mas q nada porq muchos mp3 tienen los campos de los metadatos grabados pero con espacios vacios nomas

        if ((titulo != null) && (!titulo.trim().isEmpty()))
            this.titulo = titulo;

        if ((artista != null) && (!artista.trim().isEmpty()))
            this.artista = artista;

        if (anio != -1)
            this.anio = anio;
    }

    private void cargaMetadatos() {
        try {
            File archivo = new File(this.rutaArchivo);
            if (!archivo.exists())
                return;
            //si el archivo existe pero no tiene metadatos se usa el nombre del archivo como título en vez de no mostrar nada o mostrar "artista desconocido
            //es simplemente para q el usuario tenga mas contexto(?
            this.titulo = archivo.getName().replaceAll("(?i)\\.mp3$", "");

            Mp3File mp3 = new Mp3File(this.rutaArchivo);
            this.duracionSegundos = mp3.getLengthInSeconds();

            if (mp3.hasId3v2Tag()) {
                ID3v2 tag = mp3.getId3v2Tag();
                asignarMetadatosBasicos(tag.getTitle(), tag.getArtist(), Integer.parseInt(tag.getYear()));

                byte[] portadaBytes = tag.getAlbumImage();
                if (portadaBytes != null)
                    this.portada = new Image(new ByteArrayInputStream(portadaBytes));


            } else if (mp3.hasId3v1Tag()) {
                ID3v1 tag = mp3.getId3v1Tag();
                asignarMetadatosBasicos(tag.getTitle(), tag.getArtist(), Integer.parseInt(tag.getYear()));
                // ID3v1 no tiene portada
            }

            this.valida = true;

        } catch (Exception e) {
            //no se realiza nada, cancion.valida=false por lo q el controlador se va a encargar de avisar a la GUI
        }
    }
    public boolean isValida(){
        return this.valida;
    }
    public Image getPortada() {
        return portada;
    }

    public int getAnio() {
        return anio;
    }

    public String getArtista() {
        return artista;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMediaURI() {
        return new File(rutaArchivo).toURI().toString();
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public void setPortada(Image portada) {
        this.portada = portada;
    }

    public long getDuracionSegundos() {
        return duracionSegundos;
    }
    public String getDuracionFormateada(){
        long minutos = this.duracionSegundos/60;
        long segundos = this.duracionSegundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }
    @Override
    public String toString() {
        return "Cancion{" +
                "titulo='" + titulo + '\'' +
                ", artista='" + artista + '\'' +
                ", anio='" + anio + '\'' +
                ", rutaArchivo='" + rutaArchivo + '\'' +
                ", valida=" + valida +
                '}';
    }

}


