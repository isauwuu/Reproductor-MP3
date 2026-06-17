package controllers;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import modelo.datos.Cancion;
import ui.PlaceholderGenerator;

/**
 * Celda personalizada para mostrar las canciones en la lista de reproducción.
 * Admite un modo normal y un modo de eliminación para diálogos.
 */
public class CancionListCell extends ListCell<Cancion> {

    private final ImageView imageView = new ImageView();
    private final Label titleLabel   = new Label();
    private final Label artistLabel  = new Label();
    private final HBox hbox          = new HBox(10);
    private final VBox vbox          = new VBox(2);

    private final MainController controller;
    private final boolean modoEliminacion;

    // Constructor original — comportamiento normal
    public CancionListCell(MainController controller) {
        this(controller, false);
    }

    // Constructor para el diálogo de eliminación
    public CancionListCell(MainController controller, boolean modoEliminacion) {
        this.controller      = controller;
        this.modoEliminacion = modoEliminacion;
        inicializarComponentes();

        if (modoEliminacion) {
            getStyleClass().add("cancion-cell-eliminacion");
        }
    }

    private void inicializarComponentes() {
        imageView.setFitWidth(32);
        imageView.setFitHeight(32);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(false);

        titleLabel.getStyleClass().add("cancion-titulo");
        artistLabel.getStyleClass().add("cancion-artista");

        vbox.getChildren().addAll(titleLabel, artistLabel);
        vbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        hbox.getChildren().addAll(imageView, vbox);
        hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        hbox.setPadding(new javafx.geometry.Insets(4, 4, 4, 4));
    }

    @Override
    protected void updateItem(Cancion cancion, boolean empty) {
        super.updateItem(cancion, empty);

        if (empty || cancion == null) {
            setText(null);
            setGraphic(null);
            getStyleClass().remove("list-cell-active");
            return;
        }

        titleLabel.setText(cancion.getTitulo());
        artistLabel.setText(cancion.getArtista());

        Image portada = cancion.getPortada();
        imageView.setImage(portada != null ? portada : PlaceholderGenerator.crearPlaceholder8Bit(cancion));
        setGraphic(hbox);

        if (!modoEliminacion) markCurrentSong();
    }

    public void markCurrentSong() {
        Cancion activa = controller.getCancionActual();
        if (activa != null && activa.equals(getItem())) {
            if (!getStyleClass().contains("list-cell-active"))
                getStyleClass().add("list-cell-active");
        } else {
            getStyleClass().remove("list-cell-active");
        }
    }
}