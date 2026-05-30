package ui;

import javafx.scene.Scene;
import modelo.datos.ExtractorPaleta;
import modelo.datos.Paleta;

public class ThemeManager {

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