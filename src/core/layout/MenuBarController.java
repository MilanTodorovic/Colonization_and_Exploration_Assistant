package core.layout;

import core.data.AlertBox.AboutAlertBox;
import core.data.AlertBox.LoadDifferentSaveFile;
import core.data.AlertBox.ReloadSaveFile;
import core.data.ListenForSaveUpdate;
import core.data.ParseSaveFileXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.HashMap;

import static java.nio.file.StandardWatchEventKinds.*;

public class MenuBarController {

    private final String TEXT_FILE_EXTENSION = ".xml";
    private final FileChooser fileChooser = new FileChooser();
    private final HashMap<String, Long> hashMapOfModifiedFiles = new HashMap<>();  // String - file name, Long - timestamp when modified
    private Stage rootStage;
    private String OS_name; // Might use it for something
    private String WATCH_DIR; // TODO turn into Path obj?
    private File WATCH_FILE;
    private long LAST_MODIFIED = 0;
    private ListenForSaveUpdate watchDirService;

    public MenuBarController() {
        this.fileChooser.setTitle("Navigate to the save file");
        this.fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        this.fileChooser.getExtensionFilters().add(
                // new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("XML", "*" + TEXT_FILE_EXTENSION)
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

        File chosenSaveFile = this.fileChooser.showOpenDialog(this.rootStage);

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

            ParseSaveFileXML ps = new ParseSaveFileXML(WATCH_FILE.getAbsolutePath());
            ps.parse();

        } catch (NullPointerException e) {
            // ignore
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public void closeApplication() {
        // TODO maybe perform some saving in the background?
        this.rootStage.close();
    }

    public void openAboutStage() {
        // TODO make this better aka finish it
        String message = this.rootStage.getTitle() + "\nAuthor: Milan Todorovic" + "\nGithub:";
        new AboutAlertBox(this.rootStage.getTitle(), message).display(true);
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
                /** count() the number of times an event occurred
                 / context() name of the file plus the extension
                 / kind() the type of an event: ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE
                 */

                // Debugging purposes
                // https://stackoverflow.com/questions/16133590/why-does-watchservice-generate-so-many-operations
                // https://stackoverflow.com/questions/16777869/java-7-watchservice-ignoring-multiple-occurrences-of-the-same-event
//                newEvents.stream()
//                        .forEach(event -> System.out.printf("Event count: %d \nEvent context: %s \n Event kind: %s\n\n",
//                                event.count(), event.context(), event.kind()));
//                        //.filter(event -> ENTRY_CREATE.equals(event.kind()) && isForTextFile(event.context()))
//                        //.forEach(event -> System.out.printf("%s \n %s", event.kind(), event.context()));

                newEvents.stream()
                        .filter(event -> ENTRY_CREATE.equals(event.kind()) && isForTextFile(event.context()))
                        .forEach(event -> alertBox(event));

                newEvents.stream()
                        .filter(event -> ENTRY_MODIFY.equals(event.kind()) && isForTextFile(event.context())
                                && isWatchedFile(event.context()) && wasModifiedByHuman(event.context()))
                        .forEach(event -> new ReloadSaveFile(this.rootStage.getTitle(),
                                "Save file modified. Reload?",
                                WATCH_DIR + event.context().toString()).display(false));

                // TODO prompt filechooser and unload/reset button
                newEvents.stream()
                        .filter(event -> ENTRY_DELETE.equals(event.kind()) && isForTextFile(event.context())
                                && isWatchedFile(event.context()))
                        .forEach(event -> new LoadDifferentSaveFile(this.rootStage.getTitle(),
                                "Save file deleted! Choose a new file.",
                                this.fileChooser, this.rootStage).display(false));
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

    private void alertBox(WatchEvent event) {
        String fileName = event.context().toString();
        String path = WATCH_DIR + fileName;
        long modified = Paths.get(path).toFile().lastModified();
        if (hashMapOfModifiedFiles.containsKey(fileName)) {
            if (hashMapOfModifiedFiles.get(fileName) + 1000 < modified) {
                new LoadDifferentSaveFile(this.rootStage.getTitle(), "New save file detected. Switch to it?",
                        this.fileChooser, this.rootStage).display(false);
            }
        } else {
            new LoadDifferentSaveFile(this.rootStage.getTitle(), "New save file detected. Switch to it?",
                    this.fileChooser, this.rootStage).display(false);
            hashMapOfModifiedFiles.put(fileName, modified);
        }

    }
}
