package ui;

import javafx.scene.Scene;
import modelo.datos.ExtractorPaleta;
import modelo.datos.Paleta;

/**
 * Gestor encargado de aplicar dinámicamente paletas cromáticas sobre la escena de la UI.
 * Asocia variables CSS en el nodo raíz de la ventana para reflejar el tema visual personalizado.
 */
public class ThemeManager {

    /**
     * Aplica la paleta cromática especificada sobre el nodo raíz de la escena de JavaFX
     * sobrescribiendo las variables CSS de color del tema.
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
                "-texto: " + ExtractorPaleta.toHex(paleta.getTexto()) + ";" +
                "-brillante: " + ExtractorPaleta.toHex(paleta.getBrillante()) + ";"
        );
    }
}