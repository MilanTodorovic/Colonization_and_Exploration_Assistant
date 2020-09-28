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

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("layout/mainScene.fxml"));
        Parent root = fxmlLoader.load();
        MainSceneController controller = fxmlLoader.getController();

        primaryStage.setTitle("Colonization and Exploration Assistant v0.1");

        Scene mainScene = new Scene(root, 300, 300);

        primaryStage.setScene(mainScene);
        primaryStage.show();

//        controller.canvas.draw();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
