package core.layout;

import core.data.ListenForSaveUpdate;
import core.data.AlertBox;
import javafx.stage.FileChooser; // https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm
import javafx.stage.Stage;


// For the WatchService
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.HashMap;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

public class MenuBarController {

    private Stage rootStage;
    private String OS_name; // Might use it for something
    private static final String TEXT_FILE_EXTENSION = ".xml";
    private final FileChooser fileChooser = new FileChooser();


    private static HashMap<String,Long> hashMapOfModifiedFiles = new HashMap<>();  // String - file name, Long - timestamp when modified
    private static String WATCH_DIR; // TODO turn into Path obj?
    private static File WATCH_FILE;
    private static long LAST_MODIFIED = 0;
    private ListenForSaveUpdate watchDirService;

    public MenuBarController() {
        this.fileChooser.setTitle("Navigate to the save file");
        this.fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        this.fileChooser.getExtensionFilters().add(
                // new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("XML", "*"+TEXT_FILE_EXTENSION)
        );
        // TODO maybe log this?
        this.fileChooser.getExtensionFilters().forEach(element -> System.out.printf("File Extension(s) for Filechooser: %s\n",
                element.toString()));
    }


    // TODO set flag so that only one thread is created on load/unload
    //  what to do on changing directories? stop current thread and initiate a new one?
    public void openFileExplorer() {

        try {
            stopWatchServices();
        } catch (NullPointerException e) {
            System.out.println("WatchServices haven't yet been initialized to be stopped.");
        }

        File chosenSaveFile = fileChooser.showOpenDialog(this.rootStage);

        // If I ever decide to enable multiple save loads --------------------
//        List<File> list =
//                fileChooser.showOpenMultipleDialog(stage);
//        if (list != null) {
//            for (File file : list) {
//                openFile(file);
//            }
        // -------------------------------------------------------------------

        try {
            WATCH_FILE = chosenSaveFile.getAbsoluteFile();
            WATCH_DIR = chosenSaveFile.getParent();
            System.out.printf("%s %s \n", WATCH_DIR, WATCH_FILE);

            hashMapOfModifiedFiles.put(WATCH_FILE.getName(), WATCH_FILE.lastModified());

            // Change directory to where the save file was located
            this.fileChooser.setInitialDirectory(
                    new File(WATCH_DIR)
            );

            startWatchServices();
        } catch (NullPointerException e){
            // ignore
        }
    }

    public void closeApplication() {
        // TODO maybe perform some saving in the background?
        this.rootStage.close();
    }

    public void openAboutStage() {
        // TODO make this better aka finish it
        String message = this.rootStage.getTitle() + "\nAuthor: Milan Todorovic" + "\nGithub:";
        AlertBox.display(this.rootStage.getTitle(), message);
    }

    public void setRootStage(Stage rootStage) {
        this.rootStage = rootStage;
        System.out.println("this.rootStage set to: " + rootStage.toString());
    }

    public void setOS_name(String OS_name) {
        this.OS_name = OS_name;
        System.out.println("OS name set to: " + this.OS_name);
    }

    private void startWatchServices() {
        System.out.println("Starting WatchService");

        try {
            watchDirService =
                    new ListenForSaveUpdate(
                            Paths.get(WATCH_DIR)
                    );
            System.out.printf("Watching directory: %s\n", WATCH_DIR);
        } catch (IOException e) {
            // TODO make it a bit better
            System.out.println(e.toString());
        }

        watchDirService.start();
        watchDirService.valueProperty().addListener((observable, previousEvents, newEvents) -> {
            if (newEvents != null) {
                // count() the number of times an event occurred
                // context() name of the file plus the extension
                // kind() the type of an event: ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE

                // TODO add more options to the AlertBox class
                // TODO https://stackoverflow.com/questions/16133590/why-does-watchservice-generate-so-many-operations
                //  https://stackoverflow.com/questions/16777869/java-7-watchservice-ignoring-multiple-occurrences-of-the-same-event
                //  remove duplicate event calls with either a switch statement or by checking the context

                // make a List/Set for each file to remove duplicate events like double create?
                newEvents.stream()
                        .forEach(event -> System.out.printf("Event count: %d \nEvent context: %s \n Event kind: %s\n\n",
                                event.count(), event.context(), event.kind()));
                        //.filter(event -> ENTRY_CREATE.equals(event.kind()) && isForTextFile(event.context()))
                        //.forEach(event -> System.out.printf("%s \n %s", event.kind(), event.context()));

                newEvents.stream()
                        .filter(event -> ENTRY_CREATE.equals(event.kind()) && isForTextFile(event.context()))
                        .forEach(event -> alertBox(event));

                newEvents.stream()
                        .filter(event -> ENTRY_MODIFY.equals(event.kind()) && isForTextFile(event.context())
                                && isWatchedFile(event.context()) && wasModifiedByHuman(event.context()))
                        .forEach(event -> AlertBox.display(this.rootStage.getTitle(), "Save file modified. Reload?"));

                // TODO prompt filechooser and unload/reset button
                newEvents.stream()
                        .filter(event -> ENTRY_DELETE.equals(event.kind()) && isForTextFile(event.context())
                                && isWatchedFile(event.context()))
                        .forEach(event -> AlertBox.display(this.rootStage.getTitle(), "Save file deleted! Choose a new file."));
            }
        });
    }

    public void stopWatchServices() throws NullPointerException {
        System.out.println("WatchService stopped.\n");
        watchDirService.cancel();
    }

    private boolean isForTextFile(Path path) {
        return path != null
                && !Files.isDirectory(path)
                && path.toString().endsWith(TEXT_FILE_EXTENSION);
    }

    private boolean isWatchedFile(Path path) {
        return path != null && (WATCH_FILE.getName().equals(path.toString()));
    }

    private boolean wasModifiedByHuman(Path path) {
        if (LAST_MODIFIED + 1000 < WATCH_FILE.lastModified()) {
            LAST_MODIFIED = WATCH_FILE.lastModified();
            return true;
        } else {
            // if the file was modified within a second, then we ignore it
            return false;
        }
    }

    // TODO make a better alertbox
    private void alertBox(WatchEvent event) {
        String fileName = event.context().toString();
        long modified = Paths.get(WATCH_DIR+fileName).toFile().lastModified();
        if (hashMapOfModifiedFiles.containsKey(fileName)) {
            if (hashMapOfModifiedFiles.get(fileName) + 1000 < modified) {
                AlertBox.display(this.rootStage.getTitle(), "New save file detected. Switch to it?");
            }
        } else {
            AlertBox.display(this.rootStage.getTitle(), "New save file detected. Switch to it?");
            hashMapOfModifiedFiles.put(fileName, modified);
        }

    }
}
