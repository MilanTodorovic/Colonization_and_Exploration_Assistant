package core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("layout/mainScene.fxml"));
        primaryStage.setTitle("Colonization and Exploration Assistant v0.1");

        Scene mainScene = new Scene(root, 300, 300);

        Pane rootPane = new Pane();

        Pane mapView = new Pane();
        Pane systemView = new Pane();
        Pane planetView = new Pane();
        Pane filterView = new Pane();

        rootPane.getChildren().addAll(mapView,systemView,planetView, filterView);

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
