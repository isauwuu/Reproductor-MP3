package services;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio encargado de gestionar los cuadros de diálogo de selección del sistema de archivos.
 * Mantiene la persistencia del último directorio de importación visitado.
 */
public class FileImportService {

    private File ultimaCarpeta;

    /**
     * Muestra un diálogo para seleccionar múltiples archivos MP3 de forma interactiva.
     * 
     * @param window Ventana padre para anclar el diálogo.
     * @return Lista de archivos MP3 seleccionados.
     */
    public List<File> seleccionarArchivosMp3(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar canciones");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos MP3", "*.mp3"));
        if (ultimaCarpeta != null) {
            fileChooser.setInitialDirectory(ultimaCarpeta);
        }

        List<File> archivos = fileChooser.showOpenMultipleDialog(window);
        if (archivos != null && !archivos.isEmpty()) {
            ultimaCarpeta = archivos.get(0).getParentFile();
            return archivos;
        }
        return new ArrayList<>();
    }

    /**
     * Muestra un diálogo para seleccionar una carpeta completa y extrae todos los archivos MP3 de su interior.
     * 
     * @param window Ventana padre para anclar el diálogo.
     * @return Lista de archivos MP3 encontrados.
     */
    public List<File> seleccionarCarpetaMp3(Window window) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar carpeta de música");
        if (ultimaCarpeta != null) {
            directoryChooser.setInitialDirectory(ultimaCarpeta);
        }

        File carpeta = directoryChooser.showDialog(window);
        if (carpeta == null) return new ArrayList<>();

        ultimaCarpeta = carpeta;
        File[] archivos = carpeta.listFiles();
        if (archivos == null) return new ArrayList<>();

        List<File> mp3Files = new ArrayList<>();
        for (File archivo : archivos) {
            if (archivo.getName().toLowerCase().endsWith(".mp3")) {
                mp3Files.add(archivo);
            }
        }
        return mp3Files;
    }

    /**
     * Obtiene el último directorio de importación visitado.
     * 
     * @return El objeto File que representa el directorio.
     */
    public File getUltimaCarpeta() {
        return ultimaCarpeta;
    }

    /**
     * Establece el último directorio de importación visitado.
     * 
     * @param ultimaCarpeta El objeto File a persistir.
     */
    public void setUltimaCarpeta(File ultimaCarpeta) {
        this.ultimaCarpeta = ultimaCarpeta;
    }
}
