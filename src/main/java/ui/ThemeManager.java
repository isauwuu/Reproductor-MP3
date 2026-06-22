package ui;

import javafx.scene.Scene;
import modelo.datos.ExtractorPaleta;
import modelo.datos.Paleta;
import javafx.scene.paint.Color;

/**
 * Gestor encargado de aplicar dinámicamente paletas cromáticas sobre la escena de la UI.
 * Asocia variables CSS en el nodo raíz de la ventana para reflejar el tema visual personalizado.
 */
public class ThemeManager {

    /**
     * Aplica la paleta cromática especificada sobre el nodo raíz de la escena de JavaFX
     * sobrescribiendo las variables CSS de color del tema, incluyendo gradientes y sombras.
     * 
     * @param scene  La escena de JavaFX sobre la cual aplicar los estilos.
     * @param paleta La paleta cromática conteniendo los colores a aplicar.
     */
    public static void aplicarPaleta(Scene scene, Paleta paleta) {
        scene.getRoot().setStyle(
                "-fondo: " + ExtractorPaleta.toHex(paleta.getFondo()) + ";" +
                "-panel: " + ExtractorPaleta.toHex(paleta.getPanel()) + ";" +
                "-borde: " + ExtractorPaleta.toHex(paleta.getBorde()) + ";" +
                "-acento: " + ExtractorPaleta.toHex(paleta.getAcento()) + ";" +
                "-acentoMuted: " + ExtractorPaleta.toHex(paleta.getAcentoMuted()) + ";" +
                "-texto: " + ExtractorPaleta.toHex(paleta.getTexto()) + ";" +
                "-textoMuted: " + ExtractorPaleta.toHex(paleta.getTextoMuted()) + ";" +
                "-brillante: " + ExtractorPaleta.toHex(paleta.getBrillante()) + ";" +
                "-degradadoInicio: " + ExtractorPaleta.toHex(paleta.getDegradadoInicio()) + ";" +
                "-degradadoFin: " + ExtractorPaleta.toHex(paleta.getDegradadoFin()) + ";" +
                "-glow: " + toRgba(paleta.getGlow(), 0.35) + ";" +
                "-glowSutil: " + toRgba(paleta.getGlow(), 0.15) + ";"
        );
    }

    /**
     * Formatea un color a formato string RGBA compatible con CSS de JavaFX.
     */
    private static String toRgba(Color c, double opacity) {
        return String.format("rgba(%d, %d, %d, %.2f)", 
                (int) (c.getRed() * 255), 
                (int) (c.getGreen() * 255), 
                (int) (c.getBlue() * 255), 
                opacity);
    }
}