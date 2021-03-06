package core.data;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

import core.data.ParseSaveFileXML_2;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

// TODO https://javapapers.com/core-java/abstract-and-interface-core-java-2/difference-between-a-java-interface-and-a-java-abstract-class/


public class AlertBox {

    private abstract static class AbstractAlertBox {

        protected final Stage alertBox;
        protected final Label label;
        protected final Button closeButton;
        protected final VBox layout;
        protected final Scene scene;


        public AbstractAlertBox(String title, String message) {
            alertBox = new Stage();
            label = new Label();
            closeButton = new Button("Close");
            layout = new VBox(10);
            scene = new Scene(layout);

            alertBox.initModality(Modality.APPLICATION_MODAL);
            alertBox.setTitle(title);
            alertBox.setMinWidth(250.0f);
            alertBox.setMinHeight(250.0f);
            alertBox.setResizable(false);

            label.setText(message);

            closeButton.setOnAction(e -> {
                alertBox.close();
            });
        }

        protected void defaultLayout() {
            layout.getChildren().addAll(label, closeButton);
            layout.setAlignment(Pos.CENTER);
        }

        abstract void setLayout();

        public void display(boolean useDefaultLayout) {

            if (useDefaultLayout) {
                this.defaultLayout();
            } else {
                this.setLayout();
            }
            alertBox.setScene(scene);
            alertBox.show();
        }
    }


    public static class LoadDifferentSaveFile extends AbstractAlertBox {

        private final Button loadButton;

        public LoadDifferentSaveFile(String title, String message, FileChooser fileChooser, Stage rootStage) {
            super(title, message);
            this.loadButton = new Button("Load new file");
            this.loadButton.setOnAction(e -> {
                try {
                    chooseNewSaveFile(fileChooser, rootStage);
                } catch (IOException | ParserConfigurationException | SAXException exception) {
                    exception.printStackTrace();
                }
            });
        }

        void setLayout() {
            super.layout.getChildren().addAll(super.label, loadButton, super.closeButton);
            super.layout.setAlignment(Pos.CENTER);
        }

        void chooseNewSaveFile (FileChooser fileChooser, Stage rootStage) throws IOException, SAXException, ParserConfigurationException {
            File newSaveFile = fileChooser.showOpenDialog(rootStage);
            super.alertBox.close();
            // TODO forward file to parsing
            //  Dont forward directly. get some function to put everything on hold while parsing and redraw/reload every pane
            ParseSaveFileXML_2 ps = new ParseSaveFileXML_2(newSaveFile.getAbsolutePath());
            ps.parse();
        }
    }


    public static class ReloadSaveFile extends LoadDifferentSaveFile {

        public ReloadSaveFile(String title, String message, String path) {
            super(title, message, null, null); // ignore the fileChooser and rootStage form the parent class
            // Redefining the loadButton
            super.loadButton.setText("Reload file");
            super.loadButton.setOnAction(e -> {
                // TODO forward file to parsing
                ParseSaveFileXML_2 ps;
                try {
                    ps = new ParseSaveFileXML_2(path);
                    ps.parse();
                } catch (ParserConfigurationException | IOException | SAXException parserConfigurationException) {
                    parserConfigurationException.printStackTrace();
                }
                super.alertBox.close();
            });
        }
    }


    public static class AboutAlertBox extends AbstractAlertBox {
        // TODO add ImageView and load banner/logo, add it as a flag through an argument
        public AboutAlertBox(String title, String message) {
            super(title, message);
        }

        @Override
        void setLayout() {
            // no need to implement it
        }

    }


    public static class GenericAlertBox extends AbstractAlertBox {
        // tODO complete generic alertbox
        public GenericAlertBox(String title, String message){
            super(title, message);
        }

        @Override
        void setLayout() {
            // no need to implement it
        }
    }
}

