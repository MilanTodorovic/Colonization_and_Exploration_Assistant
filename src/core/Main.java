package core;

import core.layout.MainSceneController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;

public class Main extends Application {

    private static final String version = "v0.1";
    // TODO make 2 more controllers for each VBox
    //  make one or more event listeners for clicks and selections (clicks on Nodes or toggles)

    // TODO maybe make separate JSON files for each save file for notes

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("layout/mainScene.fxml"));
        Parent root = fxmlLoader.load();
        // TODO make a constructor in the Controller and pass a reference to the stage to call an alert window after an event
        MainSceneController controller = (MainSceneController) fxmlLoader.getController();
        controller.menuBarController.setRoot(primaryStage);

        // TODO add name of the save file to the title
        primaryStage.setTitle("Colonization and Exploration Assistant "+version);

        Scene mainScene = new Scene(root, 640, 400);

        primaryStage.setScene(mainScene);
        primaryStage.show();

//        controller.canvas.draw();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
