package modelo.datos;
import javafx.scene.paint.Color;

public class Paleta {
    private Color fondo;
    private Color panel;
    private Color borde;
    private Color acento;
    private Color texto;
    private Color brillante;

    public Paleta(Color fondo, Color panel, Color borde, Color acento, Color texto, Color brillante) {
        this.fondo=fondo;
        this.panel=panel;
        this.borde=borde;
        this.acento=acento;
        this.texto=texto;
        this.brillante=brillante;
    }

    public Color getFondo(){
        return fondo;
    }
    public Color getPanel(){
        return panel;
    }
    public Color getBorde(){
        return borde;
    }
    public Color getAcento(){
        return acento;
    }
    public Color getTexto(){
        return texto;
    }
    public Color getBrillante(){
        return brillante;
    }
}

