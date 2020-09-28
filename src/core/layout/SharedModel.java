package core.layout;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Toggle;

public class SharedModel {

    private final StringProperty text = new SimpleStringProperty("Initial text...");

    public StringProperty textProperty() {
        return text ;
    }

    public final void setText(String text) {
        textProperty().set(text);
    }

    public final String getText() {
        return textProperty().get();
    }

}
// TODO https://stackoverflow.com/questions/29639881/javafx-how-to-use-a-method-in-a-controller-from-another-controller
//  https://stackoverflow.com/questions/54840226/javafx-radio-button-binding-directionally