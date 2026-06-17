package ui;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import modelo.datos.Cancion;

/**
 * Utility class to generate pixel-art placeholder covers for audio tracks.
 */
public class PlaceholderGenerator {

    /**
     * Generates an 8-bit pixelated placeholder image based on the title and artist of the song.
     * Uses the hashcode of the details to generate unique matching background and foreground colors.
     * 
     * @param cancion Song details to base the generated colors on.
     * @return WritableImage containing the generated cover.
     */
    public static Image crearPlaceholder8Bit(Cancion cancion) {
        int w = 32, h = 32;
        WritableImage img = new WritableImage(w, h);
        PixelWriter pw = img.getPixelWriter();
        
        int hash = cancion.getTitulo().hashCode() + cancion.getArtista().hashCode();
        Color bgColor = Color.rgb(
            Math.abs((hash) % 100) + 20,
            Math.abs((hash >> 8) % 100) + 20,
            Math.abs((hash >> 16) % 100) + 20
        );
        
        Color fgColor = Color.rgb(
            Math.abs((hash >> 4) % 120) + 130,
            Math.abs((hash >> 12) % 120) + 130,
            Math.abs((hash >> 20) % 120) + 130
        );

        int[][] note = {
            {0,0,0,0,0,0,0,0},
            {0,0,0,1,1,1,1,0},
            {0,0,0,1,0,0,1,0},
            {0,0,0,1,0,0,1,0},
            {0,0,1,1,0,1,1,0},
            {0,1,1,1,0,1,1,1},
            {0,1,1,1,0,1,1,1},
            {0,1,1,1,0,1,1,1},
            {0,0,1,1,0,0,1,1}
        };

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (x < 2 || x >= w - 2 || y < 2 || y >= h - 2) {
                    pw.setColor(x, y, fgColor.darker());
                } else {
                    int nx = (x - 4) / 3;
                    int ny = (y - 4) / 3;
                    if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8 && note[ny][nx] == 1) {
                        pw.setColor(x, y, fgColor);
                    } else {
                        pw.setColor(x, y, bgColor);
                    }
                }
            }
        }
        return img;
    }
}
