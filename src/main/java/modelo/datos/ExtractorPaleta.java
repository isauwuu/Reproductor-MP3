package modelo.datos;

import de.androidpit.colorthief.ColorThief;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.awt.image.BufferedImage;

/**
 * Servicio encargado de extraer y construir una paleta cromática enriquecida y armonizada ({@link Paleta})
 * a partir de la portada de la canción actual. Utiliza ColorThief para analizar la imagen
 * y agrupar los colores según luminosidad y nivel de saturación.
 */
public class ExtractorPaleta {

    /** Paleta de colores base por defecto para el reproductor (estilo retro-cyberpunk enriquecido). */
    public static final Paleta PALETA_BASE = new Paleta(
        Color.web("#080c10"), // Fondo profundo
        Color.web("#101820"), // Panel
        Color.web("#1a3a4a"), // Borde/Separador
        Color.web("#20a8c0"), // Acento
        Color.web("#157890"), // Acento secundario (muted)
        Color.web("#78b8c8"), // Texto principal
        Color.web("#4e8898"), // Texto secundario (muted)
        Color.web("#c0e8f0"), // Texto destacado/Brillante
        Color.web("#20a8c0"), // Degradado inicio
        Color.web("#157890"), // Degradado fin
        Color.web("#20a8c0")  // Glow color
    );

    /**
     * Extrae una paleta extendida y adaptativa a partir de la imagen de portada.
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

            int[][] colores = ColorThief.getPalette(transformada, 10, 10, true);
            if (colores == null || colores.length < 4)
                return PALETA_BASE;

            ordenarPorLuminosidad(colores);

            Color acento = buscarMasSaturado(colores);
            Color acentoMuted;

            Color colorMasBrillante = toColor(colores[colores.length - 1]);
            boolean tieneBlancoDominante = (colorMasBrillante.getBrightness() > 0.82 && colorMasBrillante.getSaturation() < 0.18);

            Color fondo;
            Color panel;
            Color borde;
            Color degradadoInicio;
            Color degradadoFin;
            Color texto;
            Color brillante;

            if (acento.getSaturation() < 0.12) {
                fondo = Color.web("#060810");
                panel = Color.web("#0e1220");
                borde = Color.web("#1c223a");
                acento = Color.web("#F3F4F6");
                acentoMuted = Color.web("#9CA3AF");
                degradadoInicio = Color.web("#FFFFFF");
                degradadoFin = Color.web("#9CA3AF");
                texto = Color.web("#E5E7EB");
                brillante = Color.web("#FFFFFF");
            } else {
                double satFondo = Math.max(acento.getSaturation() * 0.85, 0.45);
                fondo = Color.hsb(acento.getHue(), satFondo, 0.08);
                double satPanel = Math.max(acento.getSaturation() * 0.75, 0.38);
                panel = Color.hsb(acento.getHue(), satPanel, 0.15);
                double satBorde = Math.max(acento.getSaturation() * 0.65, 0.32);
                borde = Color.hsb(acento.getHue(), satBorde, 0.23);

                degradadoInicio = acento;

                if (tieneBlancoDominante) {
                    degradadoFin = colorMasBrillante;
                    acentoMuted = aclarar(acento, 0.40);
                    texto = Color.web("#F3F4F6");
                    brillante = Color.web("#FFFFFF");
                } else {
                    acentoMuted = buscarSegundoMasSaturado(colores, acento);
                    degradadoFin = mezclarConNegro(acentoMuted, 0.15);
                    texto = aclarar(toColor(colores[colores.length - 2]), 0.20);
                    brillante = aclarar(toColor(colores[colores.length - 1]), 0.50);
                }
            }

            Color textoMuted = mezclarConNegro(texto, 0.35);
            Color glow = acento;

            return new Paleta(fondo, panel, borde, acento, acentoMuted, texto, textoMuted, brillante, degradadoInicio, degradadoFin, glow);
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
     * Busca el segundo color más saturado de la paleta dominantes para usar como acento secundario.
     * Si no encuentra otro distinto al acento principal, rota el tono (hue) de forma complementaria.
     */
    private static Color buscarSegundoMasSaturado(int[][] colores, Color acento) {
        int[] segundo = colores[0];
        double maxSat = 0;
        for (int[] colore : colores) {
            Color c = toColor(colore);
            if (c.equals(acento)) continue;
            double sat = saturacion(colore);
            if (sat > maxSat) {
                maxSat = sat;
                segundo = colore;
            }
        }
        Color segColor = toColor(segundo);
        if (segColor.equals(acento)) {
            double hue = (acento.getHue() + 30) % 360;
            return Color.hsb(hue, acento.getSaturation(), acento.getBrightness());
        }
        return segColor;
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
    public static Color toColor(int[] rgb) {
        return Color.rgb(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * Aclara el color especificado multiplicándolo por un factor de mezcla hacia el blanco.
     * 
     * @param c      Color base.
     * @param factor Factor de aclaramiento.
     * @return El color aclarado.
     */
    public static Color aclarar(Color c, double factor) {
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
    public static Color mezclarConNegro(Color c, double factor) {
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
