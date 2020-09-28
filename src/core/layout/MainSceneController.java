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


public class MainSceneController implements Initializable {

    @FXML
    public VBox leftVBox;
    @FXML
    public AnchorPane mapAnchorPane;
    @FXML
    public ScrollPane systemScrollPane;
    @FXML
    public AnchorPane systemPane;

    @FXML
    public VBox rightVBox;
    @FXML
    public Pane filterPane; //TODO mozda FlowPane?
    @FXML
    public ScrollPane tableScrollPane;
    @FXML
    public TableView tableView;

    // TODO uzeti velicinu prozora i uraditi podelu pa dodeliti prozorima kao maxWidth/height
    //  prozor A zauzima 2/3, prozor B 1/3, prozor C ...

    public static class ResizableCanvas extends Canvas{

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

    public ResizableCanvas canvas = new ResizableCanvas(200.0f, 200.0f);

    @Override
    public void initialize(URL location, ResourceBundle resources) {


//        parentPane.getChildren().add(canvas);
//
//        // Bind the width/height property to the wrapper Pane
//        canvas.widthProperty().bind(parentPane.widthProperty());
//        canvas.heightProperty().bind(parentPane.heightProperty());


//        AnchorPane.setBottomAnchor(canvas , 8.0);
//        AnchorPane.setRightAnchor(canvas , 5.0);
//        AnchorPane.setTopAnchor(canvas , 8.0);
//        AnchorPane.setLeftAnchor(canvas , 5.0);


    }
}
