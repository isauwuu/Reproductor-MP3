package modelo.datos;

import javafx.scene.paint.Color;

/**
 * Representa una paleta de colores extendida de 11 colores extraída de la carátula de una canción.
 * Proporciona una rica gama de colores para fondos, paneles, bordes, acentos primarios/secundarios,
 * textos con jerarquía, efectos de resplandor (glow) y degradados modernos.
 */
public class Paleta {
    private final Color fondo;
    private final Color panel;
    private final Color borde;
    private final Color acento;
    private final Color acentoMuted;
    private final Color texto;
    private final Color textoMuted;
    private final Color brillante;
    private final Color degradadoInicio;
    private final Color degradadoFin;
    private final Color glow;

    /**
     * Constructor de la paleta extendida.
     */
    public Paleta(Color fondo, Color panel, Color borde, Color acento, Color acentoMuted, 
                  Color texto, Color textoMuted, Color brillante, Color degradadoInicio, 
                  Color degradadoFin, Color glow) {
        this.fondo = fondo;
        this.panel = panel;
        this.borde = borde;
        this.acento = acento;
        this.acentoMuted = acentoMuted;
        this.texto = texto;
        this.textoMuted = textoMuted;
        this.brillante = brillante;
        this.degradadoInicio = degradadoInicio;
        this.degradadoFin = degradadoFin;
        this.glow = glow;
    }

    public Color getFondo() {
        return fondo;
    }

    public Color getPanel() {
        return panel;
    }

    public Color getBorde() {
        return borde;
    }

    public Color getAcento() {
        return acento;
    }

    public Color getAcentoMuted() {
        return acentoMuted;
    }

    public Color getTexto() {
        return texto;
    }

    public Color getTextoMuted() {
        return textoMuted;
    }

    public Color getBrillante() {
        return brillante;
    }

    public Color getDegradadoInicio() {
        return degradadoInicio;
    }

    public Color getDegradadoFin() {
        return degradadoFin;
    }

    public Color getGlow() {
        return glow;
    }
}
