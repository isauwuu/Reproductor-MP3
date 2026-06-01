package controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.web.WebView;
import modelo.datos.ExtractorPaleta;
import modelo.datos.Paleta;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class SvgController {

    private final WebView bgWebView;
    private final String svgTemplateOriginal;

    public SvgController(WebView bgWebView) {
        this.bgWebView = bgWebView;
        this.svgTemplateOriginal = cargarPlantillaDesdeDisco();
    }

    /**
     * Carga el archivo físico a la memoria RAM una única vez.
     */
    private String cargarPlantillaDesdeDisco() {
        try {
            return new String(getClass().getResourceAsStream("/views/FONOOO.svg").readAllBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Inyecta la paleta y la portada, y renderiza el resultado final en el WebView.
     */
    public void actualizarFondo(Paleta paletaActiva, Image portada) {
        if (svgTemplateOriginal.isEmpty()) return;

        try {
            // Extracción de variables hex
            String acento = ExtractorPaleta.toHex(paletaActiva.getAcento());
            String acentoDim = ExtractorPaleta.toHex(paletaActiva.getBorde());
            String acentoGlow = ExtractorPaleta.toHex(paletaActiva.getFondo());
            String panel = ExtractorPaleta.toHex(paletaActiva.getPanel());
            String borde = ExtractorPaleta.toHex(paletaActiva.getBorde());
            String texto = ExtractorPaleta.toHex(paletaActiva.getTexto());

            // Procesamiento de la carátula
            String base64Img = procesarPortada8Bits(portada);
            String displayStr = base64Img.isEmpty() ? "none" : "block";

            // Inyección en el SVG
            String svgAdaptado = svgTemplateOriginal
                    .replace("#00c8c8", acento)
                    .replace("#008888", acentoDim)
                    .replace("#003030", acentoGlow)
                    .replace("#cc6688", panel)
                    .replace("#803322", borde)
                    .replace("#997a29", texto)
                    .replace("BASE64_PORTADA_PLACEHOLDER", base64Img)
                    .replace("DISPLAY_PORTADA_PLACEHOLDER", displayStr);

            bgWebView.getEngine().loadContent("""
                <!DOCTYPE html>
                <html>
                <head>
                <style>
                    html, body { margin: 0; padding: 0; width: 100%%; height: 100%%; overflow: hidden; background: #12151f; }
                    svg { width: 100%%; height: 100%%; display: block; }
                </style>
                </head>
                <body>%s</body>
                </html>
            """.formatted(svgAdaptado));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Aplica downsampling crudo (Nearest Neighbor) a la carátula para lograr el estilo pixel-art.
     */
    private String procesarPortada8Bits(Image imagenOriginal) {
        if (imagenOriginal == null) return "";

        try {
            BufferedImage bImage = SwingFXUtils.fromFXImage(imagenOriginal, null);
            if (bImage == null) return "";

            // Resolución drásticamente baja para generar el efecto de 8-bits
            int ancho8Bit = 40;
            int alto8Bit = 28;

            BufferedImage pixelada = new BufferedImage(ancho8Bit, alto8Bit, BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g2d = pixelada.createGraphics();

            // Forzamos el uso del píxel crudo sin suavizado (sin antialiasing)
            g2d.setRenderingHint(
                    java.awt.RenderingHints.KEY_INTERPOLATION,
                    java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
            );

            g2d.drawImage(bImage, 0, 0, ancho8Bit, alto8Bit, null);
            g2d.dispose();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(pixelada, "png", os);

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}