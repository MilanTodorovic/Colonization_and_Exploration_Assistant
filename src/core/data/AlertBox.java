package core.data;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public final class AlertBox {
    // TODO add ImageView and load banner/logo, add it as a flag through an argument

    public static void display(String title, String message) {
        Stage alertBox = new Stage();
        alertBox.initModality(Modality.APPLICATION_MODAL);
        alertBox.setTitle(title);
        alertBox.setMinWidth(250.0f);
        alertBox.setMinHeight(250.0f);
        alertBox.setResizable(false);

        Label label = new Label();
        label.setText(message);

        Button btn = new Button("Close");
        btn.setOnAction(e -> {
            alertBox.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, btn);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        alertBox.setScene(scene);
        alertBox.show();
    }
}
