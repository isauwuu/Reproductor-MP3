package controllers;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.shape.Ellipse;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class TocadiscosController {

    @FXML private Group brazoMecanico;
    @FXML private Ellipse brilloVinilo;

    private FadeTransition animacionVinilo;
    private Timeline brazoPlay;
    private Timeline brazoPause;
    private Rotate rotacionBrazo;

    @FXML
    public void initialize() {
        // 1. Animación del vinilo (Ilusión de giro)
        animacionVinilo = new FadeTransition(Duration.millis(800), brilloVinilo);
        animacionVinilo.setFromValue(0.1);
        animacionVinilo.setToValue(0.7);
        animacionVinilo.setCycleCount(FadeTransition.INDEFINITE);
        animacionVinilo.setAutoReverse(true);

        // 2. EL SECRETO DE LA ROTACIÓN PERFECTA:
        // Clavamos el eje en el 0,0 relativo al Group.
        // Empezamos en -35 grados (que es la posición de descanso, en la cuna)
        rotacionBrazo = new Rotate(-35, 0, 0);
        brazoMecanico.getTransforms().add(rotacionBrazo);

        // 3. Play: Gira de la cuna (-35) hacia el disco (0 grados)
        brazoPlay = new Timeline(
                new KeyFrame(Duration.seconds(0.8), new KeyValue(rotacionBrazo.angleProperty(), 0))
        );

        // 4. Pause: Gira del disco (0) de vuelta a su cuna (-35 grados)
        brazoPause = new Timeline(
                new KeyFrame(Duration.seconds(0.8), new KeyValue(rotacionBrazo.angleProperty(), -35))
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

}