package core.layout;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;


// TODO https://gist.github.com/jewelsea/5603471
//	https://stackoverflow.com/questions/16606162/javafx-fullscreen-resizing-elements-based-upon-screen-size
//	https://stackoverflow.com/questions/38216268/how-to-listen-resize-event-of-stage-in-javafx

public class MainSceneController implements Initializable {

    @FXML
    public MenuBarController menuBarController;  // TODO change to "private"
    @FXML
    public MapViewController mapAnchorPaneController;
    @FXML
    public SystemViewController systemScrollPaneController;


    @FXML
    private VBox leftVBox;
    @FXML
    private AnchorPane mapAnchorPane;
    @FXML
    private ScrollPane systemScrollPane;


    @FXML
    private VBox rightVBox;
    @FXML
    private AnchorPane filterAnchorPane; //TODO maybe FlowPane?
    @FXML
    private ScrollPane planetScrollPane;


    // TODO uzeti velicinu prozora i uraditi podelu pa dodeliti prozorima kao maxWidth/height
    //  prozor A zauzima 2/3, prozor B 1/3, prozor C ...

    private static class ResizableCanvas extends Canvas{

        public ResizableCanvas(double width, double height) {

            setWidth(width);
            setHeight(height);
            widthProperty().addListener(evt -> draw());
            heightProperty().addListener(evt -> draw());
        }
        // Redraw canvas when size changes.
        public void draw() {

//            GraphicsContext graphics_context = this.getGraphicsContext2D();
//
//            graphics_context.setFill(Color.DARKRED);
//            for (int i=0; i<=500; i=i+50){
//                graphics_context.fillOval(i, i, 50, 50);
//            }

        }

        @Override
        public boolean isResizable() {
            return false;
        }
    }

    private ResizableCanvas canvas = new ResizableCanvas(200.0f, 200.0f);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

//        parentPane.getChildren().add(canvas);
//
//        // Bind the width/height property to the wrapper Pane
//        canvas.widthProperty().bind(parentPane.widthProperty());
//        canvas.heightProperty().bind(parentPane.heightProperty());

    }
}
