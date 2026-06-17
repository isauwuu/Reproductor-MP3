package ui;

import controllers.CancionListCell;
import controllers.MainController;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import modelo.datos.Cancion;
import modelo.datos.ListaIndices;

/**
 * Diálogo modal para la selección múltiple y eliminación de canciones.
 * Encapsula la configuración visual y propagación de hojas de estilo del tema activo.
 */
public class DeleteSongsDialog extends Dialog<ListaIndices> {

    /**
     * Construye un nuevo diálogo de eliminación de canciones.
     * 
     * @param canciones      Lista de canciones disponibles para eliminar.
     * @param stylesheets    Lista de hojas de estilo del tema activo.
     * @param rootStyle      Cadena de estilo inline del root del tema activo.
     * @param mainController Controlador principal para pasar a las celdas.
     */
    public DeleteSongsDialog(ObservableList<Cancion> canciones, ObservableList<String> stylesheets, String rootStyle, MainController mainController) {
        setTitle("Eliminar canciones");

        // Propagar hojas de estilo y variables CSS dinámicas
        getDialogPane().getStylesheets().addAll(stylesheets);
        getDialogPane().setStyle(rootStyle);

        ListView<Cancion> lista = new ListView<>();
        lista.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lista.setCellFactory(lv -> new CancionListCell(mainController, true));
        lista.getItems().addAll(canciones);

        getDialogPane().setContent(lista);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Convertir la acción del botón OK a una colección de índices
        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                ObservableList<Integer> seleccionados = lista.getSelectionModel().getSelectedIndices();
                ListaIndices indices = new ListaIndices();
                for (Integer idx : seleccionados) {
                    indices.insertar(idx);
                }
                return indices;
            }
            return null;
        });
    }
}
