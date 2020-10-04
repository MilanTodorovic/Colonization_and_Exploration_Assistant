package core.layout;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import javafx.scene.input.MouseEvent; // https://docs.oracle.com/javase/9/docs/api/javafx/scene/input/MouseEvent.html

import java.net.URL;
import java.util.ResourceBundle;

public class MapViewController implements Initializable {

    @FXML
    Canvas canvas;
    @FXML
    AnchorPane mapAnchorPane;
    @FXML
    Label canvasLabel;

    private double startX; // coordinates to track mouse drag
    private double startY; // coordinates to track mouse drag

    public double mapHeight;
    public double mapWidth;

    private double[] canvasSize = new double[2]; // stores height and width

    public MapViewController() {
        // change this to the middle of the map, get map height and width
        this.startX = 0.0f;
        this.startY = 0.0f;
    }

    public void getCanvasSize() {
        // TODO
        this.canvasSize[0] = this.canvas.getHeight();
        this.canvasSize[1] = this.canvas.getWidth();
    }

    private void setMouseCoordinates(MouseEvent event) {
        startX = event.getSceneX();
        startY = event.getSceneY();
    }

    private void mouseDraggedBy(MouseEvent event) {
        // TODO change by how much the mouse cursor has moved on the x and y axis
        //  Check if the movement is a floating point value and round it
        double x = event.getSceneX() - this.startX;
        double y =  event.getSceneY() - this.startY;
        redrawCanvas(x,y);
    }

    private void redrawCanvas(double x, double y) {
        // TODO move the visible scene by x,y
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AnchorPane.setBottomAnchor(canvas , 8.0);
        AnchorPane.setRightAnchor(canvas , 5.0);
        AnchorPane.setTopAnchor(canvas , 8.0);
        AnchorPane.setLeftAnchor(canvas , 5.0);

        // TODO check if these functions initiate dragging and stopping
        //  get the initial coords within the canvas and the coords after letting go of the mouse button
        canvas.setOnMouseDragEntered(event -> setMouseCoordinates(event));
        canvas.setOnMouseDragExited(event -> mouseDraggedBy(event));

    }
}
