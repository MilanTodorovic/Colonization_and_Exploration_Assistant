package core.layout;

import core.data.ListenForSaveUpdate;
import core.data.AlertBox;
import javafx.scene.control.Alert;
import javafx.stage.Stage;


// For the WatchService
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.stream.Stream; // TODO remove

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class MenuBarController {

    private Stage root;

    private static final String TEXT_FILE_EXTENSION = ".xml";

    // this was marked as final, but changed due to later initialization outside of the constructor
    private static String WATCH_DIR;
    private ListenForSaveUpdate watchDirService;


    // TODO set flag so that only one thread is created on load/unload
    //  what to do on changing directories? stop current thread and initiate a new one?
    public void openFileExplorer() throws IOException {
        try {
            stopWatchServices();
        } catch (NullPointerException e) {
            System.out.println("WatchServices haven't yet been initialized to be stopped.");
        }

        // some code to execute before this
        // maybe implement some checking? needs to track only xml

        WATCH_DIR = "C:\\Users\\Bibl-germanistika\\Documents\\Fax"; // TODO enable
        System.out.println(WATCH_DIR);
        try {
            watchDirService =
                    new ListenForSaveUpdate(
                            Paths.get(WATCH_DIR)
                    );
        } catch (IOException e) {
            // TODO make it a bit better
            System.out.println(e.toString());
        }

        startWatchServices();
    }

    public void closeApplication() {
        // TODO maybe perform some saving in the background?
        this.root.close();
    }

    public void openAboutStage() {
        // TODO make this better aka finish it
        String message = this.root.getTitle() + "\nAuthor: Milan Todorovic" + "\nGithub:";
        AlertBox.display(this.root.getTitle(), message);
    }

    public void setRoot(Stage root) {
        this.root = root;
        System.out.println("this.root set to " + root.toString());
    }

    private void startWatchServices() {
        System.out.println("Starting WatchService");

        watchDirService.start();
        watchDirService.valueProperty().addListener((observable, previousEvents, newEvents) -> {
            if (newEvents != null) {
                // TODO add more options to the AlertBox class
                // TODO https://stackoverflow.com/questions/16133590/why-does-watchservice-generate-so-many-operations
                //  remove duplicate event calls with either a switch statement or by checking the context
                newEvents.stream()
                        .filter(event -> ENTRY_CREATE.equals(event.kind()) && isForTextFile(event.context()))
                        .forEach(event -> System.out.printf("%s \n %s", event.kind(), event.context()));

                newEvents.stream()
                        .filter(event -> ENTRY_CREATE.equals(event.kind()) && isForTextFile(event.context()))
                        .forEach(event -> AlertBox.display(this.root.getTitle(), "New save file detected. Switch to it?"));

                newEvents.stream()
                        .filter(event -> ENTRY_MODIFY.equals(event.kind()) && isForTextFile(event.context()))
                        .forEach(event -> AlertBox.display(this.root.getTitle(), "Save file modified. Reload?"));
            }
        });
    }

    public void stopWatchServices() throws NullPointerException {
        watchDirService.cancel();
    }

    private boolean isForTextFile(Path path) {
        return path != null
                && !Files.isDirectory(path)
                && path.toString().endsWith(TEXT_FILE_EXTENSION);
    }
}
