package modelo.datos;

import de.androidpit.colorthief.ColorThief;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.awt.image.BufferedImage;

public class ExtractorPaleta {
    public static final Paleta PALETA_BASE = new Paleta(Color.web("#080c10"), Color.web("#101820"), Color.web("#1a3a4a"), Color.web("#20a8c0"), Color.web("#78b8c8"), Color.web("#c0e8f0"));
    //Color.web() convierte un String hex a un objeto Color de JavaFX

    public static Paleta extraerDe(Image imagen) {
        if (imagen == null)
            return PALETA_BASE;
        try {
            BufferedImage transformada = SwingFXUtils.fromFXImage(imagen, null);
            //Convierte la Image de JavaFX a BufferedImage de AWT. Color Thief solo entiende BufferedImage (el tipo de AWT). SwingFXUtils convierte entre los dos.
            if (transformada == null)
                return PALETA_BASE;

            //declara la matriz de 6x3 donde cada fila representa un color (R,G,B) con valores de 0 a 255
            int[][] colores = ColorThief.getPalette(transformada, 6, 10, true);
            //ColorThief analiza la imagen(transformada) y devuelve los 6 colores más dominantes usando median cut. Saltea 1 de cada 10 píxeles para ser más rápido e ignora los píxeles transparentes.
            if (colores == null || colores.length < 3)
                //En caso de q la imagen sea muy simple o muy chica, Color Thief puede no encontrar suficientes colores distintos por lo q devolvemos la paleta base
                return PALETA_BASE;

            ordenarPorLuminosidad(colores);
            // Reordena los 6 colores de más oscuro a más claro para ser una paleta estandar(?

            Color fondo = mezclarConNegro(toColor(colores[0]), 0.5);
            Color panel = toColor(colores[0]);
            Color borde = toColor(colores[colores.length / 2]);
            Color acento = buscarMasSaturado(colores);
            Color texto = aclarar(toColor(colores[colores.length - 1]), 0.2);
            Color brillante = aclarar(toColor(colores[colores.length - 1]), 0.6);

            //fondo: el color más oscuro mezclado con negro al 50% — para que el fondo sea más profundo que el panel
            //panel: el más oscuro directo — un poco más claro que el fondo
            //borde: el del medio — ni muy oscuro ni muy claro, buen separador
            //acento: el más saturado (más "vivo") de los 6 — para botones, highlights
            //texto: el más claro, levemente aclarado — legible sobre el panel
            //brillante: el más claro, muy aclarado — para títulos y elementos importantes

            return new Paleta(fondo, panel, borde, acento, texto, brillante);
        } catch (Exception e) {
            return PALETA_BASE;
        }
    }

    private static void ordenarPorLuminosidad(int[][] colores) {
        //pasas la matriz y ordena fila por fila
        for (int i = 0; i < colores.length - 1; i++) {
            for (int j = 0; j < colores.length - 1 - i; j++) {
                if (luminosidad(colores[j]) > luminosidad(colores[j + 1])) {
                    int[] aux = colores[j];
                    colores[j] = colores[j + 1];
                    colores[j + 1] = aux;
                }
            }
        }
    }

    private static Color buscarMasSaturado(int[][] colores) {
        int[] masSaturado = colores[0];
        double maxSat = 0;
        for (int[] colore : colores) {
            double sat = saturacion(colore);
            if (sat > maxSat) {
                maxSat = sat;
                masSaturado = colore;
            }
        }
        return toColor(masSaturado);
    }

    private static double luminosidad(int[] rgb) {
        return 0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2];
    }

    private static double saturacion(int[] rgb) {
        double r = rgb[0] / 255.0;
        double g = rgb[1] / 255.0;
        double b = rgb[2] / 255.0;
        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));
        return max == 0 ? 0 : (max - min) / max;
    }
    //Calcula qué tan "vivo" es un color. Si los tres canales son iguales (gris), max - min = 0 → saturación 0.
    // Si uno domina completamente sobre los otros, la saturación se acerca a 1.
    // El max == 0 ? 0 evita dividir por cero cuando el color es negro puro.

    private static Color toColor(int[] rgb) {
        return Color.rgb(rgb[0], rgb[1], rgb[2]);
    }

    private static Color aclarar(Color c, double factor) {
        return new Color(Math.min(1.0, c.getRed() + (1 - c.getRed()) * factor), Math.min(1.0, c.getGreen() + (1 - c.getGreen()) * factor), Math.min(1.0, c.getBlue() + (1 - c.getBlue()) * factor), 1.0);
    }

    private static Color mezclarConNegro(Color c, double factor) {
        return new Color(c.getRed() * (1 - factor), c.getGreen() * (1 - factor), c.getBlue() * (1 - factor), 1.0);
    }

    public static String toHex(Color c) {
        return String.format("#%02X%02X%02X", (int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255));
    }

}
