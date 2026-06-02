package controllers;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class TocadiscosController {

    @FXML private Group brazoMecanico;
    @FXML private Ellipse brilloVinilo;
    @FXML private Ellipse agujaPunta;
    @FXML private Ellipse viniloCentro;
    @FXML private Group perillaRotacion;
    private FadeTransition animacionVinilo;
    private Timeline brazoPlay;
    private Timeline brazoPause;
    private Rotate rotacionBrazo;

    @FXML
    public void initialize() {
        animacionVinilo = new FadeTransition(Duration.millis(800), brilloVinilo);
        animacionVinilo.setFromValue(0.1);
        animacionVinilo.setToValue(0.7);
        animacionVinilo.setCycleCount(FadeTransition.INDEFINITE);
        animacionVinilo.setAutoReverse(true);

        // Ángulo de reposo (-28 grados) encaja exacto en la nueva cuna dibujada
        rotacionBrazo = new Rotate(-28, 0, 0);
        brazoMecanico.getTransforms().add(rotacionBrazo);

        // Ángulo de reproducción (-14 grados) cae sobre el vinilo negro, fuera de la etiqueta
        brazoPlay = new Timeline(
                new KeyFrame(Duration.seconds(0.8), new KeyValue(rotacionBrazo.angleProperty(), -14))
        );

        brazoPause = new Timeline(
                new KeyFrame(Duration.seconds(0.8), new KeyValue(rotacionBrazo.angleProperty(), -28))
        );
    }

    public void reproducirAnimacion() {
        animacionVinilo.play();
        brazoPause.stop();
        brazoPlay.playFromStart();
    }

    public void pausarAnimacion() {
        animacionVinilo.pause();
        brazoPlay.stop();
        brazoPause.playFromStart();
    }

    public void actualizarColoresDinamicos(Color acento, Color brillante) {
        if (agujaPunta != null) {
            agujaPunta.setFill(brillante);
            agujaPunta.setStroke(brillante.brighter());
        }
        if (viniloCentro != null) {
            viniloCentro.setFill(acento);
        }
    }

}