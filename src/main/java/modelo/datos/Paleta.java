package modelo.datos;
import javafx.scene.paint.Color;

/**
 * Representa una paleta de colores extraída de la carátula o portada de una canción.
 * Contiene colores armonizados para el fondo, paneles, bordes, acentos, texto y destellos.
 */
public class Paleta {
    private Color fondo;
    private Color panel;
    private Color borde;
    private Color acento;
    private Color texto;
    private Color brillante;

    /**
     * Crea una nueva instancia de Paleta con los colores especificados.
     * 
     * @param fondo     Color de fondo general.
     * @param panel     Color para contenedores y paneles secundarios.
     * @param borde     Color para líneas de división y bordes.
     * @param acento    Color de énfasis (por ejemplo, barra de progreso activa).
     * @param texto     Color legible para el texto sobre el fondo.
     * @param brillante Color de destello o luces animadas.
     */
    public Paleta(Color fondo, Color panel, Color borde, Color acento, Color texto, Color brillante) {
        this.fondo=fondo;
        this.panel=panel;
        this.borde=borde;
        this.acento=acento;
        this.texto=texto;
        this.brillante=brillante;
    }

    /**
     * Obtiene el color de fondo general.
     * 
     * @return El color de fondo de tipo {@link Color}.
     */
    public Color getFondo(){
        return fondo;
    }

    /**
     * Obtiene el color para contenedores y paneles secundarios.
     * 
     * @return El color del panel de tipo {@link Color}.
     */
    public Color getPanel(){
        return panel;
    }

    /**
     * Obtiene el color para líneas de división y bordes.
     * 
     * @return El color del borde de tipo {@link Color}.
     */
    public Color getBorde(){
        return borde;
    }

    /**
     * Obtiene el color de énfasis para elementos destacados.
     * 
     * @return El color de acento de tipo {@link Color}.
     */
    public Color getAcento(){
        return acento;
    }

    /**
     * Obtiene el color legible para el texto.
     * 
     * @return El color de texto de tipo {@link Color}.
     */
    public Color getTexto(){
        return texto;
    }

    /**
     * Obtiene el color de destello o luces animadas.
     * 
     * @return El color brillante de tipo {@link Color}.
     */
    public Color getBrillante(){
        return brillante;
    }
}

