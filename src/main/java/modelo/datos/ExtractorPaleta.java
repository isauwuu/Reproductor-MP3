package modelo.datos;

import de.androidpit.colorthief.ColorThief;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.awt.image.BufferedImage;

/**
 * Servicio encargado de extraer y construir una paleta cromática armonizada ({@link Paleta})
 * a partir de la portada de la canción actual. Utiliza ColorThief para analizar la imagen
 * y agrupar los colores según luminosidad y nivel de saturación.
 */
public class ExtractorPaleta {

    /** Paleta de colores base por defecto para el reproductor (estilo retro-cyberpunk). */
    public static final Paleta PALETA_BASE = new Paleta(
        Color.web("#080c10"), // Fondo profundo
        Color.web("#101820"), // Panel
        Color.web("#1a3a4a"), // Borde/Separador
        Color.web("#20a8c0"), // Acento
        Color.web("#78b8c8"), // Texto normal
        Color.web("#c0e8f0")  // Texto destacado/Brillante
    );

    /**
     * Extrae una paleta de 6 colores complementarios a partir de la imagen de portada.
     * Si la imagen es nula o el proceso falla, se retorna {@link #PALETA_BASE}.
     * 
     * @param imagen Imagen de la portada de la canción (JavaFX).
     * @return Paleta cromática resultante.
     */
    public static Paleta extraerDe(Image imagen) {
        if (imagen == null)
            return PALETA_BASE;
        try {
            BufferedImage transformada = SwingFXUtils.fromFXImage(imagen, null);
            if (transformada == null)
                return PALETA_BASE;

            // Extrae 6 colores dominantes
            int[][] colores = ColorThief.getPalette(transformada, 6, 10, true);
            if (colores == null || colores.length < 3)
                return PALETA_BASE;

            ordenarPorLuminosidad(colores);

            Color fondo = mezclarConNegro(toColor(colores[0]), 0.5);
            Color panel = toColor(colores[0]);
            Color borde = toColor(colores[colores.length / 2]);
            Color acento = buscarMasSaturado(colores);
            Color texto = aclarar(toColor(colores[colores.length - 1]), 0.2);
            Color brillante = aclarar(toColor(colores[colores.length - 1]), 0.6);

            return new Paleta(fondo, panel, borde, acento, texto, brillante);
        } catch (Exception e) {
            return PALETA_BASE;
        }
    }

    /**
     * Ordena los colores en base a su nivel de luminosidad percibida (Bubble Sort).
     * 
     * @param colores Matriz conteniendo los colores RGB.
     */
    private static void ordenarPorLuminosidad(int[][] colores) {
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

    /**
     * Busca y extrae el color más vivo (más saturado) de los colores dominantes analizados.
     * 
     * @param colores Matriz con los colores RGB.
     * @return El color de JavaFX más saturado.
     */
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

    /**
     * Calcula la luminosidad percibida de un color en base a coeficientes estándar de luminancia.
     * 
     * @param rgb Arreglo de 3 enteros conteniendo los valores R, G y B.
     * @return Valor double de luminosidad.
     */
    private static double luminosidad(int[] rgb) {
        return 0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2];
    }

    /**
     * Calcula la saturación cromática de un color en formato RGB.
     * 
     * @param rgb Arreglo conteniendo R, G, B.
     * @return Nivel de saturación (de 0.0 a 1.0).
     */
    private static double saturacion(int[] rgb) {
        double r = rgb[0] / 255.0;
        double g = rgb[1] / 255.0;
        double b = rgb[2] / 255.0;
        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));
        return max == 0 ? 0 : (max - min) / max;
    }

    /**
     * Convierte un arreglo RGB de 3 enteros a un objeto Color de JavaFX.
     * 
     * @param rgb Arreglo [R, G, B].
     * @return Objeto Color.
     */
    private static Color toColor(int[] rgb) {
        return Color.rgb(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * Aclara el color especificado multiplicándolo por un factor de mezcla hacia el blanco.
     * 
     * @param c      Color base.
     * @param factor Factor de aclaramiento.
     * @return El color aclarado.
     */
    private static Color aclarar(Color c, double factor) {
        return new Color(
            Math.min(1.0, c.getRed() + (1 - c.getRed()) * factor), 
            Math.min(1.0, c.getGreen() + (1 - c.getGreen()) * factor), 
            Math.min(1.0, c.getBlue() + (1 - c.getBlue()) * factor), 
            1.0
        );
    }

    /**
     * Mezcla el color especificado con negro según un factor de opacidad.
     * 
     * @param c      Color base.
     * @param factor Factor de oscuridad.
     * @return El color oscurecido.
     */
    private static Color mezclarConNegro(Color c, double factor) {
        return new Color(c.getRed() * (1 - factor), c.getGreen() * (1 - factor), c.getBlue() * (1 - factor), 1.0);
    }

    /**
     * Convierte un objeto Color de JavaFX a su representación en formato hexadecimal string (ej: #FF00FF).
     * 
     * @param c El color a convertir.
     * @return Cadena con el formato web hexadecimal.
     */
    public static String toHex(Color c) {
        return String.format("#%02X%02X%02X", (int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255));
    }
}
