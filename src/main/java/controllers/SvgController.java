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

    private String cargarPlantillaDesdeDisco() {
        try {
            return new String(getClass().getResourceAsStream("/assets/FONOOO.svg").readAllBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void actualizarFondo(Paleta paletaActiva, Image portada, boolean isPlaying) {
        if (svgTemplateOriginal.isEmpty()) return;

        try {
            String acento = ExtractorPaleta.toHex(paletaActiva.getAcento());
            String acentoDim = ExtractorPaleta.toHex(paletaActiva.getBorde());
            String acentoGlow = ExtractorPaleta.toHex(paletaActiva.getFondo());
            String panel = ExtractorPaleta.toHex(paletaActiva.getPanel());
            String borde = ExtractorPaleta.toHex(paletaActiva.getBorde());
            String texto = ExtractorPaleta.toHex(paletaActiva.getTexto());

            String base64Img = procesarPortada8Bits(portada);
            String displayStr = base64Img.isEmpty() ? "none" : "block";

            String svgAdaptado = svgTemplateOriginal
                    .replace("#00c8c8", acento)
                    .replace("#008888", acentoDim)
                    .replace("#003030", acentoGlow)
                    .replace("#cc6688", panel)
                    .replace("#803322", borde)
                    .replace("#997a29", texto)
                    .replace("BASE64_PORTADA_PLACEHOLDER", base64Img)
                    .replace("DISPLAY_PORTADA_PLACEHOLDER", displayStr);

            String htmlTemplate = """
                <!DOCTYPE html>
                <html>
                <head>
                <style>
                    html, body { margin: 0; padding: 0; width: 100%; height: 100%; overflow: hidden; background: #12151f; }
                    svg { width: 100%; height: 100%; display: block; }
                    #svg-container { width: 100%; height: 100%; display: block; position: absolute; z-index: 1; }
                    
                    .nota-musical {
                        position: absolute;
                        color: COLOR_ACENTO; 
                        font-size: 28px;
                        font-family: 'Courier New', Courier, monospace; 
                        z-index: 2;
                        opacity: 0;
                        pointer-events: none; 
                        animation: flotarUp linear forwards;
                        text-shadow: 0px 0px 8px COLOR_ACENTO; 
                    }
                    
                    @keyframes flotarUp {
                         0%   { transform: translateY(0px)    scale(1.0) rotate(0deg);   opacity: 0; }
                         10%  { transform: translateY(-25px)  scale(1.1) rotate(-3deg);  opacity: 1; }
                         50%  { transform: translateY(-120px) scale(1.3) rotate(5deg);   opacity: 0.8; }
                         100% { transform: translateY(-250px) scale(1.6) rotate(10deg);  opacity: 0; }
                    }
                </style>
                </head>
                <body>
                    <div id="svg-container">SVG_CONTENT</div>
                    
                    <script>
                        let spawner = null;
                        const spawnInterval = 2300; // INTERVALO FIJO
                        const notas = ['♪', '♫', '♩', '♬'];
                        
                        function crearNota() {
                            const nota = document.createElement('div');
                            nota.className = 'nota-musical';
                            nota.innerText = notas[Math.floor(Math.random() * notas.length)];
                            
                            nota.style.left = (47 + Math.random() * 8) + 'vw';
                            nota.style.bottom = (30 + Math.random() * 5) + 'vh';
                            nota.style.animationDuration = (4 + Math.random() * 2) + 's';
                            
                            document.body.appendChild(nota);
                            setTimeout(() => nota.remove(), 5000); 
                        }

                        function toggleNotas(estado) {
                            if (estado && !spawner) {
                                crearNota(); 
                                spawner = setInterval(crearNota, spawnInterval);
                            } else if (!estado && spawner) {
                                clearInterval(spawner);
                                spawner = null;
                            }
                        }
                        
                        // EL HTML ARRANCA REPRODUCIENDO SI JAVA LE DICE QUE SÍ
                        toggleNotas(IS_PLAYING_PLACEHOLDER);
                    </script>
                </body>
                </html>
            """;

            String htmlFinal = htmlTemplate
                    .replace("COLOR_ACENTO", acento)
                    .replace("SVG_CONTENT", svgAdaptado)
                    .replace("IS_PLAYING_PLACEHOLDER", String.valueOf(isPlaying));

            bgWebView.getEngine().loadContent(htmlFinal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void alternarNotasAnimadas(boolean reproduciendo) {
        try {
            if (bgWebView != null && bgWebView.getEngine() != null) {
                bgWebView.getEngine().executeScript("toggleNotas(" + reproduciendo + ")");
            }
        } catch (Exception e) {
            // Ignora errores si la página no terminó de cargar
        }
    }

    private String procesarPortada8Bits(Image imagenOriginal) {
        if (imagenOriginal == null) return "";
        try {
            BufferedImage bImage = SwingFXUtils.fromFXImage(imagenOriginal, null);
            if (bImage == null) return "";
            int ancho8Bit = 40;
            int alto8Bit = 28;
            BufferedImage pixelada = new BufferedImage(ancho8Bit, alto8Bit, BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g2d = pixelada.createGraphics();
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
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