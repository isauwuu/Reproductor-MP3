package controllers;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import modelo.datos.Cancion;

public class CancionListCell extends ListCell<Cancion> {
    private final ImageView imageView = new ImageView();
    private final Label titleLabel = new Label();
    private final Label artistLabel = new Label();
    private final HBox hbox = new HBox(10);
    private final VBox vbox = new VBox(2);
    private final MainController controller;

    public CancionListCell(MainController controller) {
        this.controller = controller;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        imageView.setFitWidth(32);
        imageView.setFitHeight(32);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(false);

        titleLabel.setStyle("-fx-text-fill: inherit; -fx-font-family: 'Press Start 2P'; -fx-font-size: 10px; -fx-font-weight: bold;");
        artistLabel.setStyle("-fx-text-fill: inherit; -fx-font-family: 'Press Start 2P'; -fx-font-size: 8px; -fx-opacity: 0.7;");

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
        } else {
            titleLabel.setText(cancion.getTitulo());
            artistLabel.setText(cancion.getArtista());

            Image portada = cancion.getPortada();
            if (portada != null) {
                imageView.setImage(portada);
            } else {
                imageView.setImage(controller.crearPlaceholder8Bit(cancion));
            }
            setGraphic(hbox);

            if (getIndex() == controller.getActualPos()) {
                if (!getStyleClass().contains("list-cell-active")) {
                    getStyleClass().add("list-cell-active");
                }
            } else {
                getStyleClass().remove("list-cell-active");
            }
        }
    }
}
